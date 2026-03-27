package com.amolieres.setlistync.core.domain.song.usecase

import com.amolieres.setlistync.core.domain.song.model.SongId
import com.amolieres.setlistync.core.domain.song.model.SongNote
import com.amolieres.setlistync.core.domain.song.repository.SongNoteRepository

class GetNoteForMemberAndSongUseCase(private val repo: SongNoteRepository) {
    suspend operator fun invoke(memberId: String, songId: SongId): SongNote? =
        repo.getNoteByMemberAndSong(memberId, songId)
}
