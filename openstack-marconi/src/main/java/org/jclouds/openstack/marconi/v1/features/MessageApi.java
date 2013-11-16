/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.marconi.v1.features;

import org.jclouds.Fallbacks;
import org.jclouds.openstack.keystone.v2_0.KeystoneFallbacks;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.marconi.v1.domain.CreateMessage;
import org.jclouds.openstack.marconi.v1.domain.MessagesCreated;
import org.jclouds.openstack.marconi.v1.domain.MessageStream;
import org.jclouds.openstack.marconi.v1.functions.ParseMessages;
import org.jclouds.openstack.marconi.v1.functions.ParseMessagesCreated;
import org.jclouds.openstack.marconi.v1.options.StreamOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

/**
 * Provides access to Messages via their REST API.
 *
 * @author Everett Toews
 */
@SkipEncoding({'/', '='})
@RequestFilters(AuthenticateRequest.class)
public interface MessageApi {
   /**
    * Create message(s) on a queue.
    *
    * @param clientId A UUID for each client instance. The UUID must be submitted in its canonical form (for example,
    *                 3381af92-2b9e-11e3-b191-71861300734c). The client generates the Client-ID once. Client-ID
    *                 persists between restarts of the client so the client should reuse that same Client-ID. All
    *                 message-related operations require the use of Client-ID in the headers to ensure that messages
    *                 are not echoed back to the client that posted them, unless the client explicitly requests this.
    * @param messages The messages created on the queue. The number of messages allowed in one request are configurable
    *                 by your cloud provider. Consult your cloud provider documentation to learn the maximum.
    */
   @Named("message:create")
   @POST
   @Path("/messages")
   @ResponseParser(ParseMessagesCreated.class)
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   MessagesCreated create(@HeaderParam("Client-ID") UUID clientId,
                          @BinderParam(BindToJsonPayload.class) List<CreateMessage> messages);

   @Named("message:stream")
   @GET
   @ResponseParser(ParseMessages.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(KeystoneFallbacks.EmptyPaginatedCollectionOnNotFoundOr404.class)
   @Path("/messages")
   MessageStream stream(@HeaderParam("Client-ID") UUID clientId,
                        StreamOptions... options);
}