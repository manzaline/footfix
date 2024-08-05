package com.footfix.firebase.firestoredb;

import org.hibernate.sql.exec.ExecutionException;

import java.util.List;

public interface UserService {

  List<User> getUsers() throws ExecutionException, InterruptedException, java.util.concurrent.ExecutionException;

}
