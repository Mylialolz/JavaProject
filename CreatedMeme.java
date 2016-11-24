import com.google.gson.annotations.SerializedName;

/**
 * Created by Joris on 24/11/2016.
 */
public class CreatedMeme {

    @SerializedName("instanceImageUrl")
    private String instanceImageUrl;

    public String getInstanceImageUrl(){
        return instanceImageUrl;
    }
}
