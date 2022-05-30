package kelompok.tiga.app.data;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY, property = "category",
        visible = true)
@JsonSubTypes({

        @JsonSubTypes.Type(value = Animal.class, name = "animal"),
        @JsonSubTypes.Type(value = Plant.class, name = "plant")
})
public interface Content {

    @NonNull
    String getCategory();

    void setCategory(@NonNull String category);

    @NonNull
    String getName();

    void setName(@NonNull String name);

    @NonNull
    String getAudioLink();

    void setAudioLink(@NonNull String audioLink);

    @NonNull
    String getImageLink();

    void setImageLink(@NonNull String imageLink);
}
