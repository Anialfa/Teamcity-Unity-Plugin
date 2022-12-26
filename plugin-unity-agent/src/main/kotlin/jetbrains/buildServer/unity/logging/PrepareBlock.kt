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

package jetbrains.buildServer.unity.logging

class PrepareBlock : LogBlock {

    override val name = "Prepare Build"

    override val logFirstLine = LogType.None

    override val logLastLine = LogType.None

    override fun isBlockStart(text: String) = text.contains("---- PrepareBuild Start ----")

    override fun isBlockEnd(text: String) = text.contains("---- PrepareBuild End ----")

    override fun getText(text: String) = text
}