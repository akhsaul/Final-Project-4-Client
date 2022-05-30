package kelompok.tiga.app.data;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Animal implements Content {
    private String category;
    private String name;
    @JsonProperty("audio")
    private String audioLink;
    @JsonProperty("img")
    private String imageLink;

    @NonNull
    public String getCategory() {
        return category;
    }

    public void setCategory(@NonNull String category) {
        this.category = category;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getAudioLink() {
        return audioLink;
    }

    public void setAudioLink(@NonNull String audioLink) {
        this.audioLink = audioLink;
    }

    @NonNull
    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(@NonNull String imageLink) {
        this.imageLink = imageLink;
    }

    @NonNull
    @Override
    public String toString() {
        return "Animal{" +
                "category='" + category + '\'' +
                ", name='" + name + '\'' +
                ", audioLink='" + audioLink + '\'' +
                ", imageLink='" + imageLink + '\'' +
                '}';
    }
}
