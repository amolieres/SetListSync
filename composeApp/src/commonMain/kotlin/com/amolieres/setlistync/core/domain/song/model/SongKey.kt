package com.amolieres.setlistync.core.domain.song.model

import com.amolieres.setlistync.core.data.preferences.NoteNotation
import kotlinx.serialization.Serializable

/**
 * Represents the musical key (tonality) of a song.
 *
 * Covers all 30 standard keys (15 major + 15 minor) from the circle of fifths.
 *
 * - [englishName] — English/international notation (C, Am, F#, B♭m …)
 * - [frenchName]  — French notation (Do, Lam, Fa#, Si♭m …)
 *
 * Use [display] to render the correct name based on the user's [NoteNotation] preference.
 * Use [fromEnglishName] to parse strings coming from the GetSongBPM API (handles ASCII "b" → "♭").
 */
@Serializable
enum class SongKey(
    val englishName: String,
    val frenchName: String
) {
    // ── Major keys (circle of fifths — sharp side) ────────────────────────────
    C_MAJOR("C",  "Do"),
    G_MAJOR("G",  "Sol"),
    D_MAJOR("D",  "Ré"),
    A_MAJOR("A",  "La"),
    E_MAJOR("E",  "Mi"),
    B_MAJOR("B",  "Si"),
    F_SHARP_MAJOR("F#", "Fa#"),
    C_SHARP_MAJOR("C#", "Do#"),

    // ── Major keys (circle of fifths — flat side) ─────────────────────────────
    F_MAJOR("F",   "Fa"),
    B_FLAT_MAJOR("B♭", "Si♭"),
    E_FLAT_MAJOR("E♭", "Mi♭"),
    A_FLAT_MAJOR("A♭", "La♭"),
    D_FLAT_MAJOR("D♭", "Ré♭"),
    G_FLAT_MAJOR("G♭", "Sol♭"),
    C_FLAT_MAJOR("C♭", "Do♭"),

    // ── Minor keys (circle of fifths — sharp side) ────────────────────────────
    A_MINOR("Am",   "Lam"),
    E_MINOR("Em",   "Mim"),
    B_MINOR("Bm",   "Sim"),
    F_SHARP_MINOR("F#m",  "Fa#m"),
    C_SHARP_MINOR("C#m",  "Do#m"),
    G_SHARP_MINOR("G#m",  "Sol#m"),
    D_SHARP_MINOR("D#m",  "Ré#m"),
    A_SHARP_MINOR("A#m",  "La#m"),

    // ── Minor keys (circle of fifths — flat side) ─────────────────────────────
    D_MINOR("Dm",   "Rém"),
    G_MINOR("Gm",   "Solm"),
    C_MINOR("Cm",   "Dom"),
    F_MINOR("Fm",   "Fam"),
    B_FLAT_MINOR("B♭m", "Si♭m"),
    E_FLAT_MINOR("E♭m", "Mi♭m"),
    A_FLAT_MINOR("A♭m", "La♭m");

    /** Returns the localised display name according to the user's notation preference. */
    fun display(notation: NoteNotation): String = when (notation) {
        NoteNotation.FR -> frenchName
        NoteNotation.EN -> englishName
    }

    companion object {
        /**
         * Parses a string in English notation into a [SongKey].
         * Accepts both ASCII flat ("b") and Unicode ("♭"), e.g. "Am", "F#", "Bb", "Ebm".
         * Returns null if the value does not match any known key.
         */
        fun fromEnglishName(value: String): SongKey? {
            // Normalise ASCII flat: "Ab" → "A♭", "Bbm" → "B♭m", etc.
            val normalized = value.replace(Regex("([A-G])b"), "$1♭")
            return entries.find { it.englishName == normalized }
        }
    }
}
