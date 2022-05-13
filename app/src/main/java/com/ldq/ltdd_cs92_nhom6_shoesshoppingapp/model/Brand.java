package com.ldq.ltdd_cs92_nhom6_shoesshoppingapp.model;

public class Brand {
   public int id;
   public String name;
   public String description;
   public String image;

   public Brand(int id, String name, String description, String image) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.image = image;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getImage() {
      return image;
   }

   public void setImage(String image) {
      this.image = image;
   }
}
