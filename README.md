# java-filmorate
Filmorate project.
#  **Database design** #
The database model has been developed as per the preliminary technical requirement of Sprint #11 <br/>
<br/>
## The model includes 6th tables: ##
1. Table films { <br/>
   films_id integer [primary key] <br/>
   films_name varchar [not null] <br/>
   films_description varchar(200) <br/>
   films_release_date date <br/>
   films_duration integer [not null] <br/>
   flims_rating ENUM(G, PG, PG-13, R, NC-17) <br/>
   } <br/>
   <br/>
2. Table films_likes { <br/>
   film_id integer <br/>
   user_id integer [primary key] <br/>
   emoji ENUM(LIKE) <br/>
   <br/>
     Indexes { <br/>
      (film_id, user_id) [name:"like_id"] <br/>
     } <br/>
   } <br/>
   <br/>
3. Table genres { <br/>
   genres_id integer [primary key] <br/>
   genres_name varchar(20) <br/>
   } <br/>
   <br/>
4. Table film_genres { <br/>
   genres_id integer <br/>
   films_id integer <br/>
   } <br/>
   <br/>
5. Table users { <br/>
   users_id integer [primary key] <br/>
   email varchar [not null] <br/>
   login varchar [not null] <br/>
   name varchar <br/>
   birthday date <br/>
   } <br/>
   <br/>
6. Table friends  { <br/>
   inviter integer [not null] <br/>
   invitee integer [not null] <br/>
   status bool <br/>
   <br/>
     Indexes { <br/>
      (inviter, invitee) [name:"friend_id"] <br/>
     } <br/>
   } <br/>
   <br/>
#### References: ####
 <br/>
Ref: "films"."films_id" < "film_genres"."films_id" <br/>
Ref: "genres"."genres_id" < "film_genres"."genres_id" <br/>
Ref: "users"."users_id" < "friends"."inviter" <br/>
Ref: "users"."users_id" < "friends"."invitee" <br/>
Ref: "users"."users_id" < "films_likes"."user_id" <br/>
Ref: "films"."films_id" < "films_likes"."film_id" <br/>


## **Database model diagram** ##
 
![Filmorate database diagram](https://github.com/LeonidBS/java-filmorate/blob/main/filmorate_db_diagram.png)
 

## **SQL queries:** ##

#### _Films controller_ ####

public List<Film> findAll() { <br/>

_Query all movies (findAll())_ <br/>
SELECT * FROM films; <br/>
<br/>
<br/>
public Film findById(Integer id) { <br/>

Query film by ID  (findById()) <br/>
SELECT * <br/>
FROM films <br/>
WHERE film_id = "id"; <br/>
<br/>
_Query likes_ <br/>
SELECT user_id, <br/>
emoji <br/>
FROM films_likes <br/>
WHERE film_id = id; <br/>
<br/>
<br/>
public Film create(Film film) <br/>

_Add new film_ <br/>
INSERT INTO films (films_id, <br/>
films_name, <br/>
films_description, <br/>
films_release_date, <br/>
films_duration, <br/>
flims_rating,) <br/>
VALUES ("film.getName", <br/>
"film.getDescription", <br/>
"film.getReleaseDate", <br/>
"film.getDuration", <br/>
"film.getRating"); <br/>
<br/>
<br/>
public Film update(Film film) <br/>

_Update exist film (update())_ <br/>
UPDATE films <br/>
SET films_name = film.getName, <br/>
films_description = "film.getDescription", <br/>
films_release_date = "film.getReleaseDate", <br/>
films_duration = "film.getDuration", <br/>
flims_rating = "film.getRating" <br/>
WHERE films_id = "filmId"; <br/>
<br/>
<br/>
public Film addLike(Integer filmId, Integer userId) <br/>

_Add like  (addLike())_ <br/>
INSERT INTO likes (films_id, <br/>
user_id, <br/>
emoji) <br/>
VALUES ("filmId", <br/>
"userId", <br/>
"Emoji.LIKE"); <br/>
<br/>
<br/>
public Film removeLike(Integer filmId, Integer userId) <br/>

_Remove like (addLike())_ <br/>
DELETE * <br/>
FROM likes <br/>
WHERE films_id = "filmId" AND <br/>
user_id = "userId"; <br/>
<br/>
<br/>
public List<Film> getTopFilms(Integer count) { <br/>

_Query top films (getTopFilms())_ <br/>
SELECT l.filmId, <br/>
f.films_name, <br/>
f.films_description, <br/>
f.films_release_date, <br/>
f.films_duration, <br/>
f.flims_rating <br/>
FROM likes as l <br/>
INNER JOIN films as f ON l.filmId = f.filmId <br/>
GROUP filmId <br/>
ORDER BY COUNT(l.filmId) DESC <br/>
LIMIT("count"); <br/>
<br/>


#### _Users controller_ ####

public List<User> findAll() <br/>

_Query all users (findAll())_ <br/>
SELECT * <br/>
FROM users; <br/>
<br/>
<br/>
public User findById(Integer id) <br/>

_Query user by ID findById())_ <br/>
SELECT * <br/>
FROM users <br/>
WHERE user_id = "id"; <br/>
<br/>
<br/>
public User create(User user) <br/>

_Add new user (create())_ <br/>
INSERT INTO users (users_id, <br/>
email, <br/>
login, <br/>
name, <br/>
birthday) <br/>
VALUES ("user.getId", <br/>
"user.getEmail", <br/>
"user.getLogin", <br/>
"user.getName", <br/>
"user.getBirthday"); <br/>
<br/>
<br/>
public User update(User user) <br/>

_Update exist user (update())_ <br/>
UPDATE users <br/>
SET users_id = "user.getId", <br/>
email = "user.getEmail", <br/>
login ="user.getLogin", <br/>
name = "user.getName", <br/>
birthday = "user.getBirthday"; <br/>
<br/>
<br/>
public Friends addFriendById(Integer id, Integer friendId) <br/>

_Friend invitation (addFriendById())_ <br/>
INNER INTO friends (invitor, <br/>
invitee, <br/>
status) <br/>
VALUES ("id", <br/>
"friendId", <br/>
'FALSE'); <br/>
<br/>
_Confirmation of friend invitation_   <br/>
UPDATE friends (status) <br/>
VALUES ("true") <br/>
WHERE invitor = "invitor" AND <br/>
invitee = "invitee"; <br/>
<br/>
<br/>
public Friends deleteFriendById(Integer id, Integer friendId) <br/>

_Remove friend (deleteFriendById())_ <br/>
DELETE FROM friends <br/>
WHERE invitor = "id" AND <br/>
invitee = "friendId" OR <br/>
invitor = "friendId" AND <br/>
invitee = "id";  <br/>
<br/>
<br/>
public List<User> findFriendsById(Integer id) <br/>

_Query all friends by User's ID (findFriendsById())_ (query approved friends only) <br/>
SELECT f.invitee as friend_id, <br/>
u.email, <br/>
u.login, <br/>
u.name, <br/>
u.birthday <br/>
FROM friends as f <br/>
INNER JOIN users u ON f.friend_id = u.user_id <br/>
WHERE f.invitor = "id" AND f.status = 'TRUE' <br/>
UNION <br/>
SELECT fe.invitor as friend_id, <br/>
u.email, <br/>
u.login, <br/>
u.name, <br/>
u.birthday <br/>
FROM friends as f <br/>
INNER JOIN users u ON f.friend_id = u.user_id <br/>
WHERE  f.invitee = "id" AND f.status = 'TRUE'; <br/>
<br/>
_Query all friends by user's ID (findFriendsById())_ (query includes all approved fiends and not confirmed fiends of invitor) <br/>
SELECT f.invitee as friend_id, <br/>
u.email, <br/>
u.login, <br/>
u.name, <br/>
u.birthday <br/>
FROM friends as f <br/>
INNER JOIN users u ON f.friend_id = u.user_id <br/>
WHERE f.invitor = "id" <br/>
UNION <br/>
SELECT fe.invitor as friend_id, <br/>
u.email, <br/>
u.login, <br/>
u.name, <br/>
u.birthday <br/>
FROM friends as f <br/>
INNER JOIN users u ON f.friend_id = u.user_id <br/>
WHERE  f.invitee = "id" AND f.status = 'TRUE'; <br/>
<br/>
<br/>
public List<User> findMutualFriendsByTwoIds(Integer id, Integer friendId) <br/>

_Query mutual friends of two users by their IDs (findMutualFriendsByTwoIds())_  (query approved friends only) <br/>
SELECT f.invitee as friend_id, <br/>
u.email, <br/>
u.login, <br/>
u.name, <br/>
u.birthday <br/>
FROM friends as f <br/>
INNER JOIN users u ON f.friend_id = u.user_id <br/>
WHERE (f.invitor = "id" <br/>
AND NOT f.invitee = "friendId" <br/>
OR f.invitor = "friendId" <br/>
AND NOT f.invitee = "id") <br/>
AND f.status = 'TRUE' <br/>
UNION <br/>
SELECT f.invitor as friend_id, <br/>
u.email, <br/>
u.login, <br/>
u.name, <br/>
u.birthday <br/>
FROM friends as f <br/>
INNER JOIN users u ON f.friend_id = u.user_i <br/>
WHERE (f.invitee = "id" <br/>
AND NOT f.invitor = "friendId" <br/>
OR f.invitee = "friendId" <br/>
AND NOT .invitor = "id") <br/>
AND f.status = 'TRUE'; <br/>
<br/>
_Query mutual friends of two users by their IDs (findMutualFriendsByTwoIds())_  (query includes all approved fiends and not confirmed fiends of invitor) <br/>
SELECT f.invitee as friend_id, <br/>
u.email, <br/>
u.login, <br/>
u.name, <br/>
u.birthday <br/>
FROM friends as f <br/>
INNER JOIN users u ON f.friend_id = u.user_id <br/>
WHERE (f.invitor = "id" <br/>
AND NOT f.invitee = "friendId" <br/>
OR f.invitor = "friendId" <br/>
AND NOT f.invitee = "id") <br/>
AND f.status = 'TRUE' <br/>
UNION <br/>
SELECT f.invitor as friend_id, <br/>
u.email, <br/>
u.login, <br/>
u.name, <br/>
u.birthday <br/>
FROM friends as f <br/>
INNER JOIN users u ON f.friend_id = u.user_i <br/>
WHERE (f.invitee = "id" <br/>
AND NOT f.invitor = "friendId" <br/>
OR f.invitee = "friendId" <br/>
AND NOT .invitor = "id") <br/>
AND f.status = 'TRUE'; <br/>
