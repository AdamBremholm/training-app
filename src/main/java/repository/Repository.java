package repository;

import model.User;

import java.util.List;

public interface Repository {

    List<User> list();
    User save(User user);
    User get(String userId);
    User update(User user);
    void delete(String userId);
    List<User> find(String keyWord);

}
