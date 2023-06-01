package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user=new User(name,mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist=new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        boolean check=false;
        Artist artist1=null;
       for(Artist artist:artists){
           if(artist.getName().equals(artistName)){
               check=true;
               artist1=artist;
           }
       }
       if(check==false){
            artist1=new Artist(artistName);
           artists.add(artist1);
       }
        Album album=new Album(title);
        albums.add(album);
        artistAlbumMap.put(artist1,albums);
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        boolean check =false;
        Album album1=null;
        for(Album album:albums){
            if(album.getTitle().equals(albumName)){
                check=true;
                album1=album;
            }
        }
        if(check==false)
            throw new Exception("Album does not exist");
        Song song=new Song(title,length);
        songs.add(song);
        albumSongMap.put(album1,songs);
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        Playlist playlist=new Playlist(title);
        playlists.add(playlist);
        List<Song>songs1=new ArrayList<>();
        for(Song song:songs){
            if(song.getLength()==length)songs1.add(song);
        }
        playlistSongMap.put(playlist,songs1);
        User user1=null;
        for(User user:users){
            if(user.getMobile().equals(mobile))
                user1=user;
        }
        if(user1==null)throw new Exception("User does not exist");
        creatorPlaylistMap.put(user1,playlist);
        if(!playlistListenerMap.containsKey(playlist))
            playlistListenerMap.put(playlist,new ArrayList<>());
        List<User>users1=playlistListenerMap.get(playlist);
        users1.add(user1);
        playlistListenerMap.put(playlist,users1);
        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        Playlist playlist=new Playlist(title);
        playlists.add(playlist);
        List<Song>songs1=new ArrayList<>();
        HashSet<String>titles=new HashSet<>();
        for(String s:songTitles)
            titles.add(s);
        for(Song song:songs){
            if(titles.contains(song))
                songs1.add(song);
        }
        playlistSongMap.put(playlist,songs1);
        User user1=null;
        for(User user:users){
            if(user.getMobile().equals(mobile))
                user1=user;
        }
        if(user1==null)throw new Exception("User does not exist");
        creatorPlaylistMap.put(user1,playlist);
        if(!playlistListenerMap.containsKey(playlist))
            playlistListenerMap.put(playlist,new ArrayList<>());
        List<User>users1=playlistListenerMap.get(playlist);
        users1.add(user1);
        playlistListenerMap.put(playlist,users1);
        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
      Playlist playlist=null;
      boolean check1=false,check=false;
      for(Playlist playlist1:playlists){
          if(playlist1.getTitle().equals(playlistTitle)) {
              check = true;
              playlist=playlist1;
          }
      }
      if(check==false)throw new Exception("Playlist does not exist");
      User user1=null;
      for(User user:users){
          if(user.getMobile().equals(mobile)) {
              check1 = true;
              user1=user;
          }
      }
      if(check1==false)throw new Exception("User does not exist");
      boolean check2=false;
      if(!creatorPlaylistMap.containsKey(user1)){
          List<User>users1=playlistListenerMap.get(playlist);
          for(User user:users1){
              if(user==user1){
                  check2=true;
              }
          }
          if(check2==false){
              users1.add(user1);
              playlistListenerMap.put(playlist,users1);
          }
      }
      return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
       boolean check=false,check1=false;
       User user1=null;
       Song song1=null;
       for(User user:users){
           if(user.getMobile().equals(mobile)) {
               check = true;
               user1=user;
           }
       }
       if(check==false)throw new Exception("User does not exist");
       for(Song song:songs){
           if(song.getTitle().equals(songTitle)) {
               check1 = true;
               song1=song;
           }
       }
       if (check1==false)throw new Exception("Song does not exist");
       if(song1.getLikes()==1){
           return song1;
       }
       song1.setLikes(1);
       for(Song song:songs){
           if(song.getTitle().equals(song1.getTitle())){
               songs.remove(song);
               songs.add(song1);
           }
       }
       List<User>users1=new ArrayList<>();
       if(songLikeMap.containsKey(song1))
       {
           users1=songLikeMap.get(song1);
       }
        users1.add(user1);
       songLikeMap.put(song1,users1);
       Album album=null;
       for(Map.Entry<Album,List<Song>>entry:albumSongMap.entrySet()){
          List<Song>songs1=entry.getValue();
          for(Song song:songs1){
              if(song==song1){
                  album=entry.getKey();
                  break;
              }
          }
       }
       Artist artist=null;
       for(Map.Entry<Artist,List<Album>>entry:artistAlbumMap.entrySet()){
           List<Album>albums1=entry.getValue();
           for(Album album1:albums1){
               if(album1==album){
                   artist=entry.getKey();
                   break;
               }
           }
       }
       int like=0;
       artist.setLikes(like++);
       for (Artist artist1:artists){
           if(artist1.getName().equals(artist.getName())){
               artists.remove(artist1);
               artists.add(artist);
           }
       }
       return song1;
    }

    public String mostPopularArtist() {
        String name="";
        int likes=0;
        for(Artist artist:artists){
            if(artist.getLikes()>likes){
                likes=artist.getLikes();
                name=artist.getName();
            }
        }
        return name;
    }

    public String mostPopularSong() {
        String name="";
        int likes=0;
        for(Song song:songs){
            if(song.getLikes()>likes){
                likes=song.getLikes();
                name=song.getTitle();
            }
        }
        return name;
    }
}
