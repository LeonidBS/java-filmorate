package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserMaker;
import ru.yandex.practicum.filmorate.exception.IdPassingException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Slf4j
@Component
@Qualifier("db")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM users",
                new UserMaker());
    }

    @Override
    public User findById(Integer id) {
        return jdbcTemplate.query("SELECT * FROM users WHERE id=?",
                        new Object[]{id}, new UserMaker())
                .stream().findAny().orElse(null);
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?,?,?,?)";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(),
                user.getName(), user.getBirthday());

        user.setId(jdbcTemplate.query("SELECT MAX(id) AS last_id FROM users",
                        (rs, rowNum) -> rs.getInt("last_id"))
                .stream()
                .findAny().orElse(0));

        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email=?, login=?, " +
                " name=?, birthday=? WHERE id=?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(),
                user.getName(), user.getBirthday(), user.getId());

        return user;
    }

    @Override
    public Friends addFriends(Integer inviter, Integer invitee) {
        String isTrueFriendsExistSql = "SELECT COUNT(*) > 0 AS is_friends FROM FRIENDS " +
                "WHERE (inviter IN (?,?) AND invitee IN (?,?)) AND status=true";
        Boolean isTrueFriendsExist = jdbcTemplate.query(isTrueFriendsExistSql,
                        (rs, rowNum) -> rs.getBoolean("is_friends"),
                        inviter, invitee, inviter, invitee)
                .stream().findAny().orElse(false);
        if (isTrueFriendsExist) {
            log.error("Пользователи: " + findById(inviter) + " и " + findById(invitee) +
                    "  уже являются друзьями");
            throw new ValidationException("Пользователи: " + findById(inviter) +
                    " и " + findById(invitee) + "  уже являются друзьями");
        }

        String isInvitationDoneSql = "SELECT COUNT(*) > 0 AS is_friends FROM FRIENDS " +
                "WHERE (inviter = ? AND invitee = ?) AND status=false";
        Boolean isInvitationDone = jdbcTemplate.query(isInvitationDoneSql,
                        (rs, rowNum) -> rs.getBoolean("is_friends"),
                        inviter, invitee)
                .stream().findAny().orElse(false);
        if (isInvitationDone) {
            log.error("Пользователь " + findById(inviter) +
                    " уже сделал приглашение пользователю " + findById(invitee));
            throw new ValidationException("Пользователь " + findById(inviter) +
                    " уже сделал приглашение пользователю " + findById(invitee));
        }

        Friends friends = new Friends(inviter, invitee);
        String isInvitationExistSql = "SELECT COUNT(*) > 0 AS is_invitation FROM FRIENDS " +
                "WHERE inviter=? " +
                "AND invitee=?  " +
                "AND status = false";
        Boolean isInvitationExist = jdbcTemplate.query(isInvitationExistSql,
                        (rs, rowNum) -> rs.getBoolean("is_invitation"),
                        invitee, inviter)
                .stream().findAny().orElse(false);

        if (isInvitationExist) {
            jdbcTemplate.update("UPDATE friends SET status=true " +
                            " WHERE inviter=? AND invitee=?",
                    invitee, inviter);
            friends.setStatus(true);
        } else {
            jdbcTemplate.update("INSERT INTO friends(inviter, invitee, status) " +
                    "VALUES(?, ?, false)", inviter, invitee);
            friends.setStatus(false);
        }

        return friends;
    }

    @Override
    public Friends deleteFriends(Integer inviter, Integer invitee) {
        String isFriendsExistSql = "SELECT COUNT(*) > 0 AS is_friends FROM FRIENDS " +
                "WHERE inviter IN (?,?) AND invitee IN (?,?)";
        Boolean isFriendsExist = jdbcTemplate.query(isFriendsExistSql,
                        (rs, rowNum) -> rs.getBoolean("is_friends"),
                        inviter, invitee, inviter, invitee)
                .stream().findAny().orElse(false);
        if (isFriendsExist) {
            log.debug("Пользователи с ID {}, {} удалены из друзей: ", inviter, invitee);
            jdbcTemplate.update("DELETE FROM friends " +
                            "WHERE inviter  IN (?,?) AND invitee IN (?,?)",
                    inviter, invitee, inviter, invitee);

            return new Friends(inviter, invitee);
        } else {
            log.error("Друзей с ID {}, {} не существует", inviter, invitee);
            throw new IdPassingException("Не существует  друзей с ID :" +
                    inviter + ", " + invitee);
        }
    }

    @Override
    public List<User> findFriendsById(Integer id) {
        String allFriendsSql = "SELECT u.*                              \n" +
                "FROM friends f " +
                "INNER JOIN users u ON f.invitee = u.id " +
                "WHERE f.inviter = ? " +
                "AND f.status = TRUE " +
                "UNION " +
                "SELECT u.* " +
                "FROM friends f " +
                "INNER JOIN users u ON f.invitee = u.id " +
                "WHERE f.inviter = ?";
        return jdbcTemplate.query(allFriendsSql, new Object[]{id, id}, new UserMaker());
    }

    @Override
    public List<User> findMutualFriendsByTwoIds(Integer id, Integer friendId) {
        String allFriendsSql = "SELECT * FROM " +
                "(SELECT u.* " +
                "FROM friends as f " +
                "INNER JOIN users u ON f.INVITEE = u.id " +
                "WHERE f.INVITER = ? " +
                "AND f.status = TRUE " +
                "UNION " +
                "SELECT u.* " +
                "FROM friends as f " +
                "INNER JOIN users u ON f.invitee = u.id " +
                "WHERE f.inviter = ?) as f1 " +
                "INNER JOIN " +
                "(SELECT us.* " +
                "FROM friends as fs " +
                "INNER JOIN users us ON fs.INVITEE = us.id " +
                "WHERE fs.INVITER = ? " +
                "AND fs.status = TRUE " +
                "UNION " +
                "SELECT us.* " +
                "FROM friends as fs " +
                "INNER JOIN users us ON fs.invitee = us.id " +
                "WHERE fs.inviter = ?) as f2 " +
                "ON f1.id = f2.id ";
        return jdbcTemplate.query(allFriendsSql, new Object[]{id, id, friendId, friendId}, new UserMaker());
    }
}
