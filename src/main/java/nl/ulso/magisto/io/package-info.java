/*
 * Copyright 2014 Vincent Oostindie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

/**
 * Another filesystem abstraction? Really?
 */

/**
 * Magisto is doing a lot of file system access: reading files, loading template files, copying files, reading and
 * writing directories... All that file system access is here, nicely isolated, so that it can easily be swapped out
 * in unit tests.
 */
package nl.ulso.magisto.io;