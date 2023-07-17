package org.koitharu.kotatsu.parsers.site.madara.es

import org.jsoup.nodes.Document
import org.koitharu.kotatsu.parsers.MangaLoaderContext
import org.koitharu.kotatsu.parsers.MangaSourceParser
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaChapter
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.site.madara.MadaraParser
import org.koitharu.kotatsu.parsers.util.attrAsRelativeUrlOrNull
import org.koitharu.kotatsu.parsers.util.generateUid
import org.koitharu.kotatsu.parsers.util.mapChapters
import org.koitharu.kotatsu.parsers.util.parseFailed
import org.koitharu.kotatsu.parsers.util.selectFirstOrThrow
import java.text.SimpleDateFormat

@MangaSourceParser("MANHWALATINO", "Manhwa Latino", "es")
internal class ManhwaLatino(context: MangaLoaderContext) :
	MadaraParser(context, MangaSource.MANHWALATINO, "manhwa-latino.com", 10) {

	override val datePattern = "MM/dd/yyyy"
	override val isNsfwSource = true
	override val withoutAjax = true


	override suspend fun getChapters(manga: Manga, doc: Document): List<MangaChapter> {
		val root2 = doc.body().selectFirstOrThrow("div.content-area")
		val dateFormat = SimpleDateFormat(datePattern, sourceLocale)
		return root2.select(selectchapter).mapChapters(reversed = true) { i, li ->
			val a = li.selectFirst("a")
			val href = a?.attrAsRelativeUrlOrNull("href") ?: li.parseFailed("Link is missing")
			val link = href + stylepage
			val dateText = li.selectFirst("a.c-new-tag")?.attr("title") ?: li.selectFirst(selectdate)?.text()
			val dateText2 = if (dateText == "¡Recién publicado!") {
				"1 mins ago"
			} else {
				dateText
			}
			val name = li.selectFirst("a:contains(Capitulo)")?.text() ?: a.ownText()
			MangaChapter(
				id = generateUid(href),
				name = name,
				number = i + 1,
				url = link,
				uploadDate = parseChapterDate(
					dateFormat,
					dateText2,
				),
				source = source,
				scanlator = null,
				branch = null,
			)
		}
	}
}