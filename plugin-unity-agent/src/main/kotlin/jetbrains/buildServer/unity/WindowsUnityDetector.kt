/*
 * Copyright 2000-2021 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.unity

import com.intellij.openapi.diagnostic.Logger
import com.vdurmont.semver4j.Semver
import jetbrains.buildServer.util.PEReader.PEUtil
import java.io.File

class WindowsUnityDetector : UnityDetectorBase() {

    override val editorPath = "Editor"
    override val editorExecutable = "Unity.exe"
    override val appConfigDir = "$userHome/AppData/Roaming"

    override fun findInstallations() = sequence {
        getHintPaths().distinct().forEach { path ->
            LOG.debug("Looking for Unity installation in $path")

            val executable = getEditorPath(path)
            if (!executable.exists()) {
                LOG.debug("Cannot find $executable")
                return@forEach
            }

            val version = PEUtil.getProductVersion(executable)

            LOG.info("!_! version= $version")


            if(version != null) {

                val temp=Semver("${version.p1}.${version.p2}.${version.p3}", Semver.SemverType.LOOSE) to path
                LOG.info("!_! ${temp.toString()} !_! ${version.p1}.${version.p2}.${version.p3}")
                yield(temp)
            }
            else {
                LOG.info("+_+ Cannot get version from $executable")
                //  此时由于无法直接获得编辑器的版本，但是在确认本地有对应的编辑器时，采用截取路径中的版本号的方式获得编辑器版本
                var findVersion = FindVersionInPath(executable.toString())

                yield(Semver(findVersion, Semver.SemverType.LOOSE) to path)
            }
        }
    }

    fun FindVersionInPath(strlen:String):String
    {
        val arry=strlen.split("\\").toTypedArray()
        var backstr=""
        for(item in arry)
        {
            if(item.contains("."))
            {
                if(item.indexOf(".")!=item.lastIndexOf("."))
                    backstr= item
            }
        }
        //例如 此时找到了“2019.4.40f1”,再减去“f1”。中文版编辑器是减去“f1c1”，都找“f”即可。

        return backstr.take(backstr.indexOf("f"))
    }

    override fun getHintPaths() = sequence {
        yieldAll(super.getHintPaths())

        val programFiles = hashSetOf<String>()

        System.getenv("ProgramFiles")?.let { programFiles.add(it) }
        System.getenv("ProgramFiles(X86)")?.let { programFiles.add(it) }
        System.getenv("ProgramW6432")?.let { programFiles.add(it) }

        programFiles.forEach { path ->
            if (path.isEmpty()) return@forEach
            yieldAll(findUnityPaths(File(path)))
        }
    }

    companion object {
        private val LOG = Logger.getInstance(WindowsUnityDetector::class.java.name)
    }
}
