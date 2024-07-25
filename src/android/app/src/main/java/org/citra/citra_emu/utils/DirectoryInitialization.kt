// Copyright 2023 Citra Emulator Project
// Licensed under GPLv2 or any later version
// Refer to the license.txt file included.

package io.github.mandarin3ds.mandarin.utils

import android.content.Context
import android.net.Uri
import io.github.mandarin3ds.mandarin.MandarinApplication
import io.github.mandarin3ds.mandarin.NativeLibrary
import io.github.mandarin3ds.mandarin.utils.PermissionsHandler.hasWriteAccess
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A service that spawns its own thread in order to copy several binary and shader files
 * from the Mandarin APK to the external file system.
 */
object DirectoryInitialization {
    @Volatile
    private var directoryState: DirectoryInitializationState? = null
    var userPath: String? = null
    val internalUserPath
        get() = MandarinApplication.appContext.getExternalFilesDir(null)!!.canonicalPath
    private val isMandarinDirectoryInitializationRunning = AtomicBoolean(false)

    val context: Context get() = MandarinApplication.appContext

    @JvmStatic
    fun start(): DirectoryInitializationState? {
        if (!isMandarinDirectoryInitializationRunning.compareAndSet(false, true)) {
            return null
        }

        if (directoryState != DirectoryInitializationState.MANDARIN_DIRECTORIES_INITIALIZED) {
            directoryState = if (hasWriteAccess(context)) {
                if (setMandarinUserDirectory()) {
                    MandarinApplication.documentsTree.setRoot(Uri.parse(userPath))
                    NativeLibrary.createLogFile()
                    NativeLibrary.logUserDirectory(userPath.toString())
                    NativeLibrary.createConfigFile()
                    GpuDriverHelper.initializeDriverParameters()
                    DirectoryInitializationState.MANDARIN_DIRECTORIES_INITIALIZED
                } else {
                    DirectoryInitializationState.CANT_FIND_EXTERNAL_STORAGE
                }
            } else {
                DirectoryInitializationState.EXTERNAL_STORAGE_PERMISSION_NEEDED
            }
        }
        isMandarinDirectoryInitializationRunning.set(false)
        return directoryState
    }

    @JvmStatic
    fun areMandarinDirectoriesReady(): Boolean {
        return directoryState == DirectoryInitializationState.MANDARIN_DIRECTORIES_INITIALIZED
    }

    fun resetMandarinDirectoryState() {
        directoryState = null
        isMandarinDirectoryInitializationRunning.compareAndSet(true, false)
    }

    val userDirectory: String?
        get() {
            checkNotNull(directoryState) {
                "DirectoryInitialization has to run at least once!"
            }
            check(!isMandarinDirectoryInitializationRunning.get()) {
                "DirectoryInitialization has to finish running first!"
            }
            return userPath
        }

    fun setMandarinUserDirectory(): Boolean {
        val dataPath = PermissionsHandler.mandarinDirectory
        if (dataPath.toString().isNotEmpty()) {
            userPath = dataPath.toString()
            android.util.Log.d("[Mandarin Frontend]", "[DirectoryInitialization] User Dir: $userPath")
            return true
        }
        return false
    }

    enum class DirectoryInitializationState {
        MANDARIN_DIRECTORIES_INITIALIZED,
        EXTERNAL_STORAGE_PERMISSION_NEEDED,
        CANT_FIND_EXTERNAL_STORAGE
    }
}
