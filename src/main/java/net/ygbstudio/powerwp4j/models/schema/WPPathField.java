package net.ygbstudio.powerwp4j.models.schema;

import net.ygbstudio.powerwp4j.base.extension.URLFieldsEnum;
import org.jspecify.annotations.NullMarked;

@NullMarked
public enum WPPathField implements URLFieldsEnum {
  // fields are comma-separated in the URL after the fields_base value.
  FIELDS_BASE("?_fields="),
  FIELD_AUTHOR("author"),
  FIELD_ID("id"),
  FIELD_EXCERPT("excerpt"),
  FIELD_TITLE("title"),
  FIELD_LINK("link");

  private final String value;

  WPPathField(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
