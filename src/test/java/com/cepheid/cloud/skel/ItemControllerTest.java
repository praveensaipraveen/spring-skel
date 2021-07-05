package com.cepheid.cloud.skel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.util.stream.Stream;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.cepheid.cloud.skel.model.Description;
import com.cepheid.cloud.skel.model.Item;
import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
public class ItemControllerTest extends TestBase {

  private ArrayList<Item> savedItems =new ArrayList<>();


  @Before
  public void initialise() {
	  Long i=new Long(0);
	  Stream.of("Lord of the rings", "Hobbit", "Silmarillion", "Unfinished Tales and The History of Middle-earth")
      .forEach(name -> {
        Item item = new Item();
        item.setId(new Long(this.savedItems.size()+1));
        item.setName(name);
        item.setState(Item.State.valid);
        
        String[] tags = name.split(" ");
        List<Description> des=new ArrayList<>();
        for(String tag:tags){
            Description description = new Description(tag);
            des.add(description);
      	}
        item.setDescriptions(des);
        savedItems.add(item);
      });
  }
  

  @Test
  public void testGetItems() throws Exception {
    Builder itemController = getBuilder("/app/api/1.0/items");    
    Collection<Item> items = itemController.get(new GenericType<Collection<Item>>() {
    });   
    assertTrue(savedItems.equals(items));
  }
  
  @Test
  public void testGetItem() throws Exception {
	  Builder itemController = getBuilder("/app/api/1.0/items/"+this.savedItems.get(0).getId());
	  Item item = itemController.get(Item.class);
	  assertTrue(item.equals(savedItems.get(0)));
  }
  
  @Test
  public void testCreateItem() throws Exception {
	  Builder itemController = getBuilder("/app/api/1.0/items/create");
	

	  List<Description> des=new ArrayList<>();
	  Description description = new Description();
	  description.setDescription("Iron");
	  des.add(description);
	  description = new Description();
	  description.setDescription("Man");
	  des.add(description);
	  description = new Description();
	  description.setDescription("III");
	  des.add(description);
	  
	  String postContent = "{ \"name\":\"Iron Man III\", "
	  							+ "\"state\":\"valid\", "
	  							+ "\"descriptions\" : [{\"description\":\"Iron\"},{\"description\":\"Man\"},{\"description\":\"III\"}]}";


	  Response response = itemController.post(Entity.json(postContent));
	  Item item = response.readEntity(Item.class);
	  	
		assertEquals(200,response.getStatus());
		assertEquals(Response.Status.OK,response.getStatusInfo());
		assertEquals("Iron Man III",item.getName());
		assertEquals(Item.State.valid,item.getState());
		assertEquals(des,item.getDescriptions());
  }
  
  @Test
  public void testUpdateItem() throws Exception {
	  Builder itemController = getBuilder("/app/api/1.0/items/"+savedItems.get(1).getId());
	  
	  Item itemSaved = savedItems.get(1);
	  itemSaved.setName("Iron Man III");
	  
	  String postContent = "{ \"name\":\"Iron Man III\"}";
	  Response response = itemController.put(Entity.json(postContent));
	  Item item = response.readEntity(Item.class);
	  assertEquals(200,response.getStatus());
	  assertEquals(Response.Status.OK,response.getStatusInfo());
	  assertEquals(itemSaved,item);
  }
  
  @Test
  public void testDeleteItem() throws Exception {
	  Builder itemController = getBuilder("/app/api/1.0/items/"+savedItems.get(2).getId());
	  Response response = itemController.delete();
	  assertEquals(200,response.getStatus());
	  assertEquals(Response.Status.OK,response.getStatusInfo());
  }
  
  @Test
  public void testGetByName() throws Exception {
	  Builder itemController = getBuilder("/app/api/1.0/items/name/rings");
	  Collection<Item> itemsByName = itemController.get(new GenericType<Collection<Item>>() {
	    });
	  List<Item> list=new ArrayList<>();
      list.add(savedItems.get(0));
	  assertEquals(list,itemsByName);
  }
  
  @Test
  public void testGetByState() throws Exception {
	  Builder itemController = getBuilder("/app/api/1.0/items/state/valid");
	  Collection<Item> itemsByState = itemController.get(new GenericType<Collection<Item>>() {
	    });
	  for(Item item:itemsByState) {
		  assertEquals(Item.State.valid,item.getState());
	  }
	  
  }
}
