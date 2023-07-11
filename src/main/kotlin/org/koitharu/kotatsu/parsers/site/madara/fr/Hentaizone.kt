package org.koitharu.kotatsu.parsers.site.madara.fr

import org.jsoup.nodes.Document
import org.koitharu.kotatsu.parsers.MangaLoaderContext
import org.koitharu.kotatsu.parsers.MangaSourceParser
import org.koitharu.kotatsu.parsers.model.MangaChapter
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.site.madara.MadaraParser
import org.koitharu.kotatsu.parsers.util.attrAsRelativeUrl
import org.koitharu.kotatsu.parsers.util.domain
import org.koitharu.kotatsu.parsers.util.generateUid
import org.koitharu.kotatsu.parsers.util.mapChapters
import org.koitharu.kotatsu.parsers.util.parseHtml
import org.koitharu.kotatsu.parsers.util.removeSuffix
import org.koitharu.kotatsu.parsers.util.selectFirstOrThrow
import org.koitharu.kotatsu.parsers.util.toAbsoluteUrl
import java.text.SimpleDateFormat
import java.util.Locale


@MangaSourceParser("HENTAIZONE", "Hentaizone", "fr")
internal class Hentaizone(context: MangaLoaderContext) :
	MadaraParser(context, MangaSource.HENTAIZONE, "hentaizone.xyz", pageSize = 10) {

	override val datePattern = "MMM d, yyyy"
	override val sourceLocale: Locale = Locale.FRENCH

	override val isNsfwSource = true


	override suspend fun loadChapters(mangaUrl: String, document: Document): List<MangaChapter> {
		val url = mangaUrl.toAbsoluteUrl(domain).removeSuffix('/') + "/ajax/chapters/"
		val dateFormat = SimpleDateFormat(datePattern, sourceLocale)
		val doc = webClient.httpPost(url, emptyMap()).parseHtml()

		return doc.select("li.wp-manga-chapter").mapChapters(reversed = true) { i, li ->
			val a = li.selectFirstOrThrow("a")
			val href = a.attrAsRelativeUrl("href") + "?style=list"

			// correct parse date missing a "."
			val dateOrg = li.selectFirst("span.chapter-release-date i")?.text() ?: "janv 1, 2000"
			val dateCorrectParse = dateOrg
				.replace("janv", "janv.")
				.replace("févr", "févr.")
				.replace("avr", "avr.")
				.replace("juil", "juil.")
				.replace("sept", "sept.")
				.replace("nov", "nov.")
				.replace("oct", "oct.")
				.replace("déc", "déc.")
			MangaChapter(
				id = generateUid(href),
				url = href,
				name = a.text(),
				number = i + 1,
				branch = null,
				uploadDate = parseChapterDate(
					dateFormat,
					dateCorrectParse,
				),
				scanlator = null,
				source = source,
			)
		}
	}

}