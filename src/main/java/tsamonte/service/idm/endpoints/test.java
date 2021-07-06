package tsamonte.service.idm.endpoints;

import tsamonte.service.idm.logger.ServiceLogger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("hello")
public class test {
    @GET
    public Response hello() {
        System.err.println("Hello!");
        ServiceLogger.LOGGER.info("Hello!");
        return Response.status(Response.Status.OK).build();

    }
}
