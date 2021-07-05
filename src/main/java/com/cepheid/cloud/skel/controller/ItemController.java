package com.cepheid.cloud.skel.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.cepheid.cloud.skel.model.Item;
import com.cepheid.cloud.skel.repository.ItemRepository;

import io.swagger.annotations.Api;


// curl http:/localhost:9443/app/api/1.0/items

@Component
@Path("/api/1.0/items")
@Api()
public class ItemController {

  private final ItemRepository mItemRepository;

  @Autowired
  public ItemController(ItemRepository itemRepository) {
    mItemRepository = itemRepository;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
  public Collection<Item> getItems() {
    return this.mItemRepository.findAll();
  }
  
  @GET
  @Path("/{mId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Optional<Item> getItem(@PathParam("mId") Long mId) {
		  return this.mItemRepository.findById(mId);
  }
  
  @GET
  @Path("/name/{name}")
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<Item> getItemsByName(@PathParam("name") String name) {
		  return this.mItemRepository.findByNameContainingIgnoreCase(name);
  }

  @GET
  @Path("/state/{state}")
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<Item> getItemsByState(@PathParam("state") String state) {
	  	try {
	  	  Item.State state_enum = Item.State.valueOf(state);
		  return this.mItemRepository.findByState(state_enum);
	  	}catch(Exception e) {
	  		return new ArrayList<>();
	  	}
  }
  
  @POST
  @Path("/create")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Item createItem(@RequestBody Item item) {	 
		  return this.mItemRepository.save(item);
  }
 
  @PUT
  @Path("/{mId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Object updateItem(@PathParam("mId") Long mId,@RequestBody Item item_input) {
	  		Optional<Item> itemOp = this.mItemRepository.findById(mId);
	  		
	  		if(itemOp.isPresent()){
	  			Item item =itemOp.get();
	  			if(item_input.getName()!=null)
	  				item.setName(item_input.getName());
	  			if(item_input.getDescriptions()!=null)
	  				item.setDescriptions(item_input.getDescriptions());
	  			if(item_input.getState()!=null)
	  				item.setState(item_input.getState());
	  			return this.mItemRepository.save(item);
	  		}else {
	  			return new EntityNotFoundException();
	  		}
  }
  
  @DELETE
  @Path("/{mId}")
  public String deleteItem(@PathParam("mId") Long mId) {
	  try {
	   	this.mItemRepository.deleteById(mId);
	   	return mId+"deleted successfully";
	  }catch(Exception e) {
		return e.getMessage();  
	  }
		
  }
  
}
