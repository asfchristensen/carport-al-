package com.example.carportal.repositories;

import java.util.List;

public interface IRepository<T> {

    public T getOneEntity(int ID);

    public List<T> getAllEntities(); // Might delete this one

    public boolean create(T entity);

    public boolean update (int ID);

    public boolean delete (int ID);







}
