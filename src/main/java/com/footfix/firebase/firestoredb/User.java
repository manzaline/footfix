package com.footfix.firebase.firestoredb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class User {

  private String id;
  private String name;
  private String email;

  // firebase timestamp type
  private Date create_dt;
  private Date update_dt;
}
