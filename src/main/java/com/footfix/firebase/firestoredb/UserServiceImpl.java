package com.footfix.firebase.firestoredb;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserDao userDao;

  @Override
  public List<User> getUsers() throws ExecutionException, InterruptedException {
    return userDao.getUsers();
  }
}
