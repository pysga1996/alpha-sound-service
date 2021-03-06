package com.alpha.controller;

import com.alpha.model.dto.PlaylistDTO;
import com.alpha.service.PlaylistService;
import com.alpha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*", exposedHeaders = {HttpHeaders.SET_COOKIE})
@RestController
@RequestMapping("/api/playlist")
public class PlaylistRestController {
    private PlaylistService playlistService;
    private UserService userService;

    @Autowired
    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/list")
    public ResponseEntity<Page<PlaylistDTO>> playlistList(Pageable pageable) {
        OAuth2AuthenticatedPrincipal currentUser = userService.getCurrentUser();
        String username = currentUser.getName();
        Page<PlaylistDTO> playlistList = playlistService.findAllByUsername(username, pageable);
        boolean isEmpty = playlistList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(playlistList, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/detail", params = "id")
    public ResponseEntity<PlaylistDTO> playlistDetail(@RequestParam("id") Long id) {
        Optional<PlaylistDTO> playlist = playlistService.findById(id);
        if (playlist.isPresent()) {
            if (playlistService.checkPlaylistOwner(id)) {
                return new ResponseEntity<>(playlist.get(), HttpStatus.OK);
            } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/create")
    public ResponseEntity<Void> createPlaylist(@Valid @RequestBody PlaylistDTO playlist) {
        if (playlist == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            playlist.setUsername(this.userService.getCurrentUser().getName());
            this.playlistService.save(playlist);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/edit", params = "id")
    public ResponseEntity<String> editPlaylist(@Valid @RequestBody PlaylistDTO playlist, @RequestParam("id") Long id) {
        Optional<PlaylistDTO> oldPlaylist = playlistService.findById(id);
        if (oldPlaylist.isPresent()) {
            if (playlistService.checkPlaylistOwner(id)) {
                playlist.setId(id);
                playlist.setUsername(this.userService.getCurrentUser().getName());
                playlist.setSongs(oldPlaylist.get().getSongs());
                this.playlistService.save(playlist);
                return new ResponseEntity<>(HttpStatus.OK);
            } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else return new ResponseEntity<>("Playlist not found!", HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(value = "/delete", params = "id")
    public ResponseEntity<Void> deletePlaylist(@RequestParam Long id) {
        if (playlistService.checkPlaylistOwner(id)) {
            playlistService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/add-song")
    public ResponseEntity<Void> addSongToPlaylist(@RequestParam("song-id") Long songId, @RequestParam("playlist-id") Long playlistId) {
        if (playlistService.checkPlaylistOwner(playlistId)) {
            boolean result = playlistService.addSongToPlaylist(songId, playlistId);
            if (result) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/remove-song")
    public ResponseEntity<Void> removeSongFromPlaylist(@RequestParam("song-id") Long songId, @RequestParam("playlist-id") Long playlistId) {
        if (playlistService.checkPlaylistOwner(playlistId)) {
            boolean result = playlistService.deleteSongFromPlaylist(songId, playlistId);
            if (result) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list-to-add")
    public ResponseEntity<Iterable<PlaylistDTO>> showPlaylistListToAdd(@RequestParam("song-id") Long songId) {
        Iterable<PlaylistDTO> filteredPlaylistList = playlistService.getPlaylistListToAdd(songId);
        int size = 0;
        if (filteredPlaylistList instanceof Collection) {
            size = ((Collection<?>) filteredPlaylistList).size();
        }
        if (size == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(filteredPlaylistList, HttpStatus.OK);
    }
}
