package net.wyvest.simplerpc.gui

import gg.essential.api.EssentialAPI
import gg.essential.api.utils.Multithreading
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.EnumChatFormatting
import net.wyvest.simplerpc.SimpleRPC
import net.wyvest.simplerpc.utils.Updater
import net.wyvest.simplerpc.utils.Updater.shouldUpdate
import net.wyvest.simplerpc.utils.Updater.updateUrl
import java.io.File
import kotlin.math.max


class DownloadConfirmGui(private val parent: GuiScreen?) : GuiScreen() {
    override fun initGui() {
        buttonList.add(GuiButton(0, width / 2 - 100, height - 50, 200, 20, EnumChatFormatting.GREEN.toString() + "Yes"))
        buttonList.add(GuiButton(1, width / 2 - 100, height - 28, 200, 20, EnumChatFormatting.RED.toString() + "No"))
        super.initGui()
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> {
                mc.displayGuiScreen(GuiMainMenu())
                Multithreading.runAsync {
                    if (Updater.download(
                            updateUrl,
                            File("mods/${SimpleRPC.NAME}-${Updater.latestTag.substringAfter("v")}.jar")
                        ) && Updater.download(
                            "https://github.com/Wyvest/Deleter/releases/download/v1.2/Deleter-1.2.jar",
                            File("config/Wyvest/Deleter-1.2.jar")
                        )
                    ) {
                        EssentialAPI.getNotifications()
                            .push(SimpleRPC.NAME, "The ingame updater has successfully installed the newest version.")
                        Updater.addShutdownHook()
                        shouldUpdate = false
                    } else {
                        EssentialAPI.getNotifications().push(
                            SimpleRPC.NAME,
                            "The ingame updater has NOT installed the newest version as something went wrong."
                        )
                    }
                }
            }
            1 -> {
                if (parent == null) {
                    mc.displayGuiScreen(parent)
                } else {
                    EssentialAPI.getGuiUtil().openScreen(parent)
                }
            }
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        GlStateManager.pushMatrix()
        GlStateManager.scale(2f, 2f, 0f)
        drawCenteredString(
            fontRendererObj,
            EnumChatFormatting.DARK_PURPLE.toString() + SimpleRPC.NAME,
            width / 4,
            3,
            -1
        )
        GlStateManager.popMatrix()
        GlStateManager.pushMatrix()
        GlStateManager.scale(1f, 1f, 0f)
        val lines = listOf(
            "Are you sure you want to update?",
            "You can download it ingame at any time via the configuration screen.",
            "(This will update from v${SimpleRPC.VERSION} to ${Updater.latestTag})"
        )
        var offset = max(85 - lines.size * 10, 10)

        for (line in lines) {
            drawCenteredString(mc.fontRendererObj, EnumChatFormatting.RED.toString() + line, width / 2, offset, -1)
            offset += mc.fontRendererObj.FONT_HEIGHT + 2
        }
        GlStateManager.popMatrix()
        super.drawScreen(mouseX, mouseY, partialTicks)
    }
}