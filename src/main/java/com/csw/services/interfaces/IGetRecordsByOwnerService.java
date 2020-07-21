package com.csw.services.interfaces;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

public interface IGetRecordsByOwnerService {
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path(value = "/GetRecordsByOwner")
    String getRecordsByOwnerResponse(@QueryParam("owner") String owner);
}
