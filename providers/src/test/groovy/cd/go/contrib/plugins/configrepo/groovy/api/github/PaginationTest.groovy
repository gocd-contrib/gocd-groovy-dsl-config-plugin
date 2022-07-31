/*
 * Copyright 2022 Thoughtworks, Inc.
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
 * limitations under the License.
 */

package cd.go.contrib.plugins.configrepo.groovy.api.github

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

class PaginationTest {
  @Test
  void 'parses labeled page numbers'() {
    final String links = '<https://api.github.com/repositories/1234/pulls?page=3>; rel="next", <https://api.github.com/repositories/1234/pulls?page=7>; rel="last"'
    Pagination pg = new Pagination(links)

    assertTrue(pg.hasNext())
    assertTrue(pg.hasLast())
    assertEquals(3, pg.next())
    assertEquals(7, pg.last())
  }

  @Test
  void 'returns -1 when a label is not present'() {
    final String links = '<https://api.github.com/repositories/1234/pulls?page=3>; rel="prev", <https://api.github.com/repositories/1234/pulls?page=7>; rel="first"'
    Pagination pg = new Pagination(links)

    assertFalse(pg.hasNext())
    assertFalse(pg.hasLast())
    assertEquals(-1, pg.next())
    assertEquals(-1, pg.last())
  }

  @Test
  void 'returns -1 on junk'() {
    final String links = 'blueberries!'
    Pagination pg = new Pagination(links)

    assertFalse(pg.hasNext())
    assertFalse(pg.hasLast())
    assertEquals(-1, pg.next())
    assertEquals(-1, pg.last())
  }
}
