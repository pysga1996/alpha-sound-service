package com.alpha.service.impl;

import com.alpha.model.dto.UserDTO;
import com.alpha.model.entity.Artist;
import com.alpha.model.entity.Like;
import com.alpha.model.entity.Song;
import com.alpha.model.entity.Tag;
import com.alpha.repositories.LikeRepository;
import com.alpha.repositories.SongRepository;
import com.alpha.repositories.TagRepository;
import com.alpha.service.SongService;
import com.alpha.service.UserService;
import com.alpha.util.formatter.StringAccentRemover;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;

    private final LikeRepository likeRepository;

    private final UserService userService;

    private final TagRepository tagRepository;

    private final AudioStorageService audioStorageService;

    @Autowired
    public SongServiceImpl(SongRepository songRepository, LikeRepository likeRepository, UserService userService, TagRepository tagRepository, AudioStorageService audioStorageService) {
        this.songRepository = songRepository;
        this.likeRepository = likeRepository;
        this.userService = userService;
        this.tagRepository = tagRepository;
        this.audioStorageService = audioStorageService;
    }

    @Override
    public Page<Song> findAll(Pageable pageable, String sort) {
        if (sort != null && sort.equals("releaseDate")) {
            return songRepository.findAllByOrderByReleaseDateDesc(pageable);
        } else if (sort != null && sort.equals("listeningFrequency")) {
            return songRepository.findAllByOrderByListeningFrequencyDesc(pageable);
        } else if (sort != null && sort.equals("likesCount")) {
//            return songRepository.findAllByOrderByUsers_Size(pageable);
            return null;
        } else {
            return songRepository.findAll(pageable);
        }
    }

    @Override
    public Iterable<Song> findAll() {
        return songRepository.findAll();
    }

    @Override
    public Iterable<Song> findTop10By(String sort) {
        return songRepository.findFirst10ByOrderByListeningFrequencyDesc();
    }

    @Override
    public Page<Song> findAllByOrderByReleaseDateDesc(Pageable pageable) {
        return songRepository.findAllByOrderByReleaseDateDesc(pageable);
    }

    @Override
    public Page<Song> findAllByOrderByDisplayRatingDesc(Pageable pageable) {
        return songRepository.findAllByOrderByDisplayRatingDesc(pageable);
    }

    @Override
    public Page<Song> findAllByOrderByListeningFrequencyDesc(Pageable pageable) {
        return songRepository.findAllByOrderByListeningFrequencyDesc(pageable);
    }

    @Override
    public Page<Song> findAllByLikesCount(Pageable pageable) {
//        return songRepository.findAllByOrderByUsers_Size(pageable);
        return null;
    }

    @Override
    public Page<Song> findAllByUploader_Id(Long id, Pageable pageable) {
        return songRepository.findAllByUploader_Id(id, pageable);
    }

    @Override
    public Optional<Song> findById(Long id) {
        return songRepository.findById(id);
    }

    @Override
    public Iterable<Song> findAllByTitle(String title) {
        return songRepository.findAllByTitle(title);
    }

    @Override
    public Iterable<Song> findAllByTitleContaining(String name) {
        if (name.equals(StringAccentRemover.removeStringAccent(name))) {
            return songRepository.findAllByUnaccentTitleContainingIgnoreCase(name);
        } else {
            return songRepository.findAllByTitleContainingIgnoreCase(name);
        }

    }

    @Override
    public Page<Song> findAllByTitleContaining(String name, Pageable pageable) {
        return songRepository.findAllByTitleContaining(name, pageable);
    }

    @Override
    public Page<Song> findAllByArtistsContains(Artist artist, Pageable pageable) {
        return songRepository.findAllByArtistsContains(artist, pageable);
    }

    @Override
    public Page<Song> findAllByUsersContains(UserDTO user, Pageable pageable) {
        Page<Song> songList = songRepository.findAllByUsersContains(user, pageable);
        setLike(songList);
        return songList;
    }

    @Override
    public Page<Song> findAllByTag_Name(String name, Pageable pageable) {
        return songRepository.findAllByTag_Name(name, pageable);
    }

    @Override
    public Iterable<Song> findAllByAlbum_Id(Long id) {
        return songRepository.findAllByAlbum_Id(id);
    }

    @Override
    public Song save(Song song) {
        Collection<Tag> tags = song.getTags();
        for (Tag tag : tags) {
            if (tagRepository.findByName("tag") == null) {
                tagRepository.saveAndFlush(tag);
            }
        }
        String unaccentTitle = StringAccentRemover.removeStringAccent(song.getTitle());
        song.setUnaccentTitle(unaccentTitle.toLowerCase());
        songRepository.saveAndFlush(song);
        return song;
    }

    @Override
    public void deleteById(Long id) {
        Optional<Song> song = songRepository.findById(id);
        if (song.isPresent()) {
            songRepository.deleteById(id);
//            String fileUrl = song.get().getUrl();
//            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
//            return audioStorageService.deleteLocalStorageFile(audioStorageService.audioStorageLocation, filename);
            audioStorageService.deleteFirebaseStorageFile(song.get());
        }
    }

    @Override
    public void deleteAll(Collection<Song> songs) {
        songRepository.deleteAll(songs);
    }

    @Override
    public void setFields(Song oldSongInfo, Song newSongInfo) {
        oldSongInfo.setTitle(newSongInfo.getTitle());
        oldSongInfo.setArtists(newSongInfo.getArtists());
        oldSongInfo.setGenres(newSongInfo.getGenres());
        oldSongInfo.setCountry(newSongInfo.getCountry());
        oldSongInfo.setReleaseDate(newSongInfo.getReleaseDate());
        oldSongInfo.setTags(newSongInfo.getTags());
        oldSongInfo.setTheme(newSongInfo.getTheme());
        if (newSongInfo.getUrl() != null) {
            oldSongInfo.setUrl(newSongInfo.getUrl());
        }
    }

    @Override
    public Page<Song> sortByDate(Pageable pageable) {
        return null;
    }

    @Override
    public boolean hasUserLiked(Long songId) {
        Long userId = userService.getCurrentUser().getId();
        Like like = likeRepository.findBySongIdAndUserId(songId, userId);
        return (like != null);
    }

    @Override
    public void setLike(Song song) {
        song.setLiked(hasUserLiked(song.getId()));
    }

    @Override
    public void setLike(Page<Song> songList) {
        for (Song song : songList) {
            song.setLiked(hasUserLiked(song.getId()));
        }
    }

    @Override
    public void setLike(Iterable<Song> songList) {
        for (Song song : songList) {
            song.setLiked(hasUserLiked(song.getId()));
        }
    }
}
