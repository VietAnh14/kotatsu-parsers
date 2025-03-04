package org.koitharu.kotatsu.parsers.model

import androidx.collection.ArrayMap
import org.koitharu.kotatsu.parsers.util.findById
import org.koitharu.kotatsu.parsers.util.nullIfEmpty

public data class Manga constructor(
	/**
	 * Unique identifier for manga
	 */
	@JvmField public val id: Long,
	/**
	 * Manga title, human-readable
	 */
	@JvmField public val title: String,
	/**
	 * Alternative title (for example on other language), may be null
	 */
	@JvmField public val altTitle: String?,
	/**
	 * Relative url to manga (**without** a domain) or any other uri.
	 * Used principally in parsers
	 */
	@JvmField public val url: String,
	/**
	 * Absolute url to manga, must be ready to open in browser
	 */
	@JvmField public val publicUrl: String,
	/**
	 * Normalized manga rating, must be in range of 0..1 or [RATING_UNKNOWN] if rating s unknown
	 * @see hasRating
	 */
	@JvmField public val rating: Float,
	/**
	 * Indicates that manga may contain sensitive information (18+, NSFW)
	 */
	@JvmField public val contentRating: ContentRating?,
	/**
	 * Absolute link to the cover
	 * @see largeCoverUrl
	 */
	@JvmField public val coverUrl: String?,
	/**
	 * Tags (genres) of the manga
	 */
	@JvmField public val tags: Set<MangaTag>,
	/**
	 * Manga status (ongoing, finished) or null if unknown
	 */
	@JvmField public val state: MangaState?,
	/**
	 * Author of the manga, may be null
	 */
	@JvmField public val author: String?,
	/**
	 * Large cover url (absolute), null if is no large cover
	 * @see coverUrl
	 */
	@JvmField public val largeCoverUrl: String? = null,
	/**
	 * Manga description, may be html or null
	 */
	@JvmField public val description: String? = null,
	/**
	 * List of chapters
	 */
	@JvmField public val chapters: List<MangaChapter>? = null,
	/**
	 * Manga source
	 */
	@JvmField public val source: MangaSource,
) {

	@Deprecated("Prefer constructor with contentRating instead of isNsfw")
	public constructor(
		id: Long,
		title: String,
		altTitle: String?,
		url: String,
		publicUrl: String,
		rating: Float,
		isNsfw: Boolean,
		coverUrl: String?,
		tags: Set<MangaTag>,
		state: MangaState?,
		author: String?,
		largeCoverUrl: String? = null,
		description: String? = null,
		chapters: List<MangaChapter>? = null,
		source: MangaSource,
	) : this(
		id = id,
		title = title,
		altTitle = altTitle?.nullIfEmpty(),
		url = url,
		publicUrl = publicUrl,
		rating = rating,
		contentRating = if (isNsfw) ContentRating.ADULT else null,
		coverUrl = coverUrl?.nullIfEmpty(),
		tags = tags,
		state = state,
		author = author?.nullIfEmpty(),
		largeCoverUrl = largeCoverUrl?.nullIfEmpty(),
		description = description?.nullIfEmpty(),
		chapters = chapters,
		source = source,
	)

	/**
	 * Return if manga has a specified rating
	 * @see rating
	 */
	public val hasRating: Boolean
		get() = rating > 0f && rating <= 1f

	public val isNsfw: Boolean
		get() = contentRating == ContentRating.ADULT

	public fun getChapters(branch: String?): List<MangaChapter> {
		return chapters?.filter { x -> x.branch == branch }.orEmpty()
	}

	public fun findChapterById(id: Long): MangaChapter? = chapters?.findById(id)

	public fun requireChapterById(id: Long): MangaChapter = findChapterById(id)
		?: throw NoSuchElementException("Chapter with id $id not found")

	public fun getBranches(): Map<String?, Int> {
		if (chapters.isNullOrEmpty()) {
			return emptyMap()
		}
		val result = ArrayMap<String?, Int>()
		chapters.forEach {
			val key = it.branch
			result[key] = result.getOrDefault(key, 0) + 1
		}
		return result
	}
}
