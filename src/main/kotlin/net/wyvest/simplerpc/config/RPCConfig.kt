package net.wyvest.simplerpc.config

import gg.essential.api.EssentialAPI
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import net.wyvest.simplerpc.SimpleRPC
import net.wyvest.simplerpc.SimpleRPC.NAME
import net.wyvest.simplerpc.SimpleRPC.mc
import net.wyvest.simplerpc.gui.DownloadConfirmGui
import net.wyvest.simplerpc.utils.Updater
import java.io.File

@Suppress("unused")
object RPCConfig : Vigilant(File(SimpleRPC.modDir, "${SimpleRPC.ID}.toml"), NAME) {

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Mod",
        description = "Toggle the mod.\nCan be buggy and sometimes needs a restart to take effect.",
        category = "General"
    )
    var toggled = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Automatically Turn Off Mod When HyCord",
        description = "Automatically turn off SimpleRPC when HyCord is detected.",
        category = "General"
    )
    var hycordDetect = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Show Details",
        description = "Show the details of the RPC.",
        category = "General"
    )
    var showDetails = true

    @Property(
        type = PropertyType.SELECTOR,
        name = "Select Details",
        description = "Select the type of detail for the RPC.",
        category = "General",
        options = ["Time Elapsed", "Current Server", "Current User", "Current Item Held", "Current Amount of Players"]
    )
    var details = 0

    @Property(
        type = PropertyType.SWITCH,
        name = "Show Image",
        description = "Show the image for the RPC.",
        category = "General"
    )
    var showImage = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Show Update Notification",
        description = "Show a notification when you start Minecraft informing you of new updates.",
        category = "Updater"
    )
    var showUpdateNotification = true

    @Property(
        type = PropertyType.BUTTON,
        name = "Update Now",
        description = "Update $NAME by clicking the button.",
        category = "Updater"
    )
    fun update() {
        if (Updater.shouldUpdate) EssentialAPI.getGuiUtil()
            .openScreen(DownloadConfirmGui(mc.currentScreen)) else EssentialAPI.getNotifications()
            .push(NAME, "No update had been detected at startup, and thus the update GUI has not been shown.")
    }

    init {
        initialize()
        registerListener("toggled") {
                isToggled: Boolean ->
            run {
                if (isToggled) {
                    try {
                        SimpleRPC.ipc.connect()
                    } catch (e: Exception) {
                        EssentialAPI.getNotifications().push("SimpleRPC", "There was an error trying to reconnect to the IPC. Please restart your game for your changes to take effect.")
                    }
                } else {
                    try {
                        SimpleRPC.ipc.disconnect()
                    } catch (e: Exception) {
                        EssentialAPI.getNotifications().push("SimpleRPC", "There was an error trying to disconnect to the IPC. Please restart your game for your changes to take effect.")
                    }
                }
            }
        }
    }
}