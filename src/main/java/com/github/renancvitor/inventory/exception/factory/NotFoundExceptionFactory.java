package com.github.renancvitor.inventory.exception.factory;

import com.github.renancvitor.inventory.exception.types.common.EntityNotFoundException;

public class NotFoundExceptionFactory {

    public static EntityNotFoundException category(Integer id) {
        return new EntityNotFoundException("Categoria", id);
    }

    public static EntityNotFoundException permission(Integer id) {
        return new EntityNotFoundException("Permissão", id);
    }

    public static EntityNotFoundException person(Long id) {
        return new EntityNotFoundException("Pessoa", id);
    }

    public static EntityNotFoundException user(Long id) {
        return new EntityNotFoundException("Usuário", id);
    }

    public static EntityNotFoundException product(Long id) {
        return new EntityNotFoundException("Produto", id);
    }

    public static EntityNotFoundException userType(Integer id) {
        return new EntityNotFoundException("Tipo do Usuário", id);
    }

    public static EntityNotFoundException movementType(Integer id) {
        return new EntityNotFoundException("Tipo de Movimentação", id);
    }

    public static EntityNotFoundException movement(Long id) {
        return new EntityNotFoundException("Movimentação", id);
    }

}
