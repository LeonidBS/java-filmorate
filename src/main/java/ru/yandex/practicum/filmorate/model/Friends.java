package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

/*
Задание по друзьям намеренно выполнено с расширенным функционалом
*/

@Data
public class Friends {
    @NotBlank(message = "Id приглашающего пользователя не передано")
    private final Integer inviter;

    @NotBlank(message = "Id приглашенного пользователя не передано")
    private final Integer invitee;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Friends(@JsonProperty("inviter") Integer inviter,
                   @JsonProperty("invitee") Integer invitee) {
        this.inviter = inviter;
        this.invitee = invitee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Friends friends = (Friends) o;
        return (Objects.equals(inviter, friends.inviter) && Objects.equals(invitee, friends.invitee))
                || (Objects.equals(inviter, friends.invitee) && Objects.equals(invitee, friends.inviter));
    }

    @Override
    public int hashCode() {   //Status does not count
        int hash = 17;
        if (inviter != null) {
            hash += Math.pow(inviter, 3);
        }
        if (invitee != null) {
            hash = hash += Math.pow(invitee, 3);
        }
        hash *= 31;
        return hash;
    }

}
