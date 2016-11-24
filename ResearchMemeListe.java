import com.google.gson.annotations.SerializedName;

/**
 * Created by Antoine on 24/11/2016.
 */
public class ResearchMemeListe {


    @SerializedName("generatorID")
    private String generatorId;

    @SerializedName("imageUrl")
    private String imageUrl;


    public ResearchMemeListe(){

    }


    public String getGeneratorId(){
        return generatorId;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public String getImageId(){

        String ret = null;
        if(imageUrl != null){
            final int index = imageUrl.lastIndexOf('/');
            ret = imageUrl.substring(index + 1);
            final int indexEx = ret.lastIndexOf('.');
            ret = ret.substring(0, indexEx);
            System.out.println("ret : " + ret);
        }
        return ret;

    }


}
