package mp3ready.serializer;

import com.google.gson.Gson;

public class Entity {
	

	 public String toJsonObject(){
		 try{
		String json =  new Gson().toJson( this ) ;
		return json ;
		 }catch(Exception e){
			 e.printStackTrace() ;
		 }
		 return "" ;
	 }
	 

}
