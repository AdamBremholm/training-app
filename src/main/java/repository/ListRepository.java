package repository;

import model.User;

import java.util.List;

public class ListRepository implements Repository {

    List<User> users;


    @Override
    public List<User> list() {
        return null;
    }

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public User get(String userId) {
        return null;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public void delete(String userId) {

    }

    @Override
    public List<User> find(String keyWord) {
        return null;
    }
}
