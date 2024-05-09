package ru.netology.Diploma_JD.dto;

import lombok.Getter;

@Getter
public enum Role {
    ROLE_USER,
    ROLE_ADMIN;
}

// Предусмотрела возможность выбора ролей, но для текущего задания в этом не было необходимости. В коде нигде не используется,
// на доступ не влияет