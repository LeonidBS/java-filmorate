# java-filmorate
Filmorate project. This is movies rating service provides users to add, update, rate, search, select movies


## Stack
![](https://img.shields.io/badge/language-Java 11-orange)
![](https://img.shields.io/badge/build_automation_tool-Maven-red)
![](https://img.shields.io/badge/tools-Lombok-orange)
![](https://img.shields.io/badge/framework-Spring_boot-green)
![](https://img.shields.io/badge/database-H2Database-blue)
![](https://img.shields.io/badge/database-JDBC-grey)
![](https://img.shields.io/badge/test-JUnit-grey)

## Function
1. Create User;
2. Update User;
3. Retrieve a list of all Users;
4. Retrieve information about a specific User;
5. Add friends;
6. Remove from friends;
7. Retrieve a list of friends of a specific User;
8. Retrieve a list of mutual friends.

9. Create a Film;
10. Update Film;
11. Retrieve a list of all Films;
12. Retrieve information about a specific Film;
13. Like;
14. Delete likes;
15. Change genres and film ratings of the Film Association
16. Retrieve top popular movies

#  **Database design** #
The database model has been developed as per the preliminary technical requirement of Sprint #11 <br/>
<br/>
## The model includes 6th tables: ##
1. Table films { <br/>
   id integer [primary key] <br/>
   name varchar [not null] <br/>
   description varchar(200) <br/>
   release_date date <br/>
   duration integer [not null] <br/>
   mpa_id integer [not null] <br/>
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
   id integer [primary key] <br/>
   name varchar(20) <br/>
   } <br/>
   <br/>
4. Table film_genres { <br/>
   genres_id integer <br/>
   films_id integer <br/>
   } <br/>
   <br/>
5. Table users { <br/>
   id integer [primary key] <br/>
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
Ref: "films"."id" < "film_genres"."films_id" <br/>
Ref: "genres"."genres_id" < "film_genres"."genres_id" <br/>
Ref: "users"."uid" < "friends"."inviter" <br/>
Ref: "users"."id" < "friends"."invitee" <br/>
Ref: "users"."uid" < "films_likes"."user_id" <br/>
Ref: "films"."id" < "films_likes"."film_id" <br/>


## **Database model diagram** ##
 
![Filmorate database diagram](https://github.com/LeonidBS/java-filmorate/blob/add-database/resources/filmorate_db_diagram1.png)
 

## **SQL queries:** ##

#### _Films controller_ ####

List<Film> findAll()  <br/>

SELECT f.id, <br/
f.name,  <br/
f.description,  <br/
f.release_date,  <br/
f.duration, <br/
m.id as mpa_id, <br/
m.name as mpa_name  <br/
FROM films f 
INNER JOIN mpa m ON f.mpa_id=m.id )  <br/>

public List<Film> topFilms(Integer count) {
String sql = "SELECT f.id,   <br/
"f.name,   <br/
"f.description,   <br/
"f.release_date,   <br/
"f.duration,  <br/
"m.id as mpa_id,   <br/
"m.name as mpa_name   <br/
"FROM films f   <br/
"INNER JOIN mpa m ON f.mpa_id=m.id   <br/
"LEFT JOIN FILMS_LIKES fl on f.id = fl.film_id   <br/
"GROUP BY f.id   <br/
"ORDER BY COUNT(fl.film_id) DESC   <br/
"LIMIT ?   <br/
return jdbcTemplate.query(sql, new Object[]{count},   <br/
new FilmMaker(jdbcTemplate));   <br/

#### Development of the project is planned




