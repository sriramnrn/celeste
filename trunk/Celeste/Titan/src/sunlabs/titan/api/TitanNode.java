/*
 * Copyright 2007-2010 Oracle. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * This code is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * only, as published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 *
 * Please contact Oracle Corporation, 500 Oracle Parkway, Redwood Shores, CA 94065
 * or visit www.oracle.com if you need additional information or
 * have any questions.
 */
package sunlabs.titan.api;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import javax.management.ObjectName;

import sunlabs.asdf.util.Attributes;
import sunlabs.asdf.web.XML.XHTML;
import sunlabs.asdf.web.XML.XML;
import sunlabs.asdf.web.http.HTTP;
import sunlabs.titan.node.ApplicationFramework;
import sunlabs.titan.node.BeehiveNode.NoSuchNodeException;
import sunlabs.titan.node.BeehiveObjectStore;
import sunlabs.titan.node.NeighbourMap;
import sunlabs.titan.node.NodeAddress;
import sunlabs.titan.node.Publishers;
import sunlabs.titan.node.TitanMessage;
import sunlabs.titan.node.TitanMessage.RemoteException;
import sunlabs.titan.node.object.BeehiveObjectHandler;
import sunlabs.titan.node.services.AbstractTitanService;
import sunlabs.titan.node.util.DOLRLogger;

public interface TitanNode {
    /**
     * Start the node.
     * This consists of initialising all of the listening sockets,
     * establishing our first connection via a Join operation,
     * and booting each of the internal applications in this Node.
     */
    public Thread start();
    

    public void stop();

    /**
     * Get this Node's {@link NodeAddress}.
     */
    public NodeAddress getNodeAddress();
    
    /**
     * Send the given {@link Serializable} {@code data} via a {@link TitanMessage.Type#RouteToNode} to the {@link TitanNode}
     * that is the root of the {@link TitanNodeId} {@code nodeId}.
     *
     * @param klasse The name of the {@link AbstractTitanService} to handle the reception of this message.
     * @param method The name of the method to invoke in {@code klasse} on the receiving node.
     * @param payload Serializable data as the input parameter to the {@code method} method.
     */
    public TitanMessage sendToNode(TitanNodeId nodeId, String klasse, String method, Serializable payload);
    

    /**
     * Send to a specific object-id on a specific node.
     * This directly violates the separation of nodes and objects.
     */
    @Deprecated
    public TitanMessage sendToNode(TitanNodeId nodeId, TitanGuid objectId, String klasse, String method, Serializable data);

    /**
     * Transmit a {@link TitanMessage.Type#RouteToNode}
     * message to the specified node object-id.
     *
     * @param nodeId the {@link TitanNodeId} of the destination node.
     * @param objectClass the {@link String} name of the destination object handler class.
     * @param method the {@link String} name of the method to invoke.
     * @param payload the payload of this message.
     * @throws NoSuchNodeException if the receiver cannot route the message further and is not the specified destination nodeId.
     * @throws ClassNotFoundException 
     * @throws RemoteException 
     * @throws ClassCastException 
     */
    public TitanMessage sendToNodeExactly(TitanNodeId nodeId, String klasse, String method, Serializable payload) throws NoSuchNodeException, ClassCastException, RemoteException, ClassNotFoundException;

    /**
     * Send the given {@link Serializable} {@code data} to the {@link TitanObject}
     * specified by {@link TitanGuid} {@code objectId}.
     * The object must be of the {@link BeehiveObjectHandler} {@code klasse} and the
     * method invoked is specified by String {@code method}.
     *
     * @param objectId The {@link TitanGuid} of the target {@link TitanObject}.
     * @param klasse The name of the {@link BeehiveObjectHandler} to use.
     * @param method The name of the method to invoke.
     * @param payload The data to transmit to the object.
     */
    public TitanMessage sendToObject(TitanGuid objectId, String klasse, String method, Serializable payload);


    public Attributes getConfiguration();


    public TitanNodeId getNodeId();


    public ObjectName getJMXObjectName();


    public String getSpoolDirectory();

    public DOLRLogger getLogger();


    public ThreadGroup getThreadGroup();


    public TitanMessage transmit(sunlabs.titan.node.NodeAddress gateway, TitanMessage message);
    public TitanMessage transmit(TitanMessage message);


    public Publishers getObjectPublishers();


    public ObjectStore getObjectStore();


    public TitanMessage receive(TitanMessage message);


    public NeighbourMap getNeighbourMap();


    public TitanMessage replyTo(TitanMessage message, Serializable payload);


    public TitanGuid getNetworkObjectId();


    public <C> C getService(Class<? extends C> klasse);


    /**
     * Get (dynamically loading and instantiating, if necessary) an instance of the named class cast to the given {@link TitanService}.
     *
     * @param <C>
     * @param klasse
     * @param serviceName
     * @return an instance of the named class cast to the given {@link TitanService}
     * @throws ClassCastException if the loaded class is <em>not</em> an instance of {@code klasse}.
     * @throws ClassNotFoundException if the class cannot be found.
     */
    public TitanService getService(final String serviceName);


    public ApplicationFramework getServiceFramework();


    public XML.Content toXML();

    /**
     * Produce an {@link sunlabs.asdf.web.XML.XHTML.EFlow XHTML.EFlow} element containing an XHTML formatted representation of this node.
     */
    public XHTML.EFlow toXHTML(URI uri, Map<String,HTTP.Message> props) throws URISyntaxException;

    /**
     * 
     * @param objectId
     * @return
     * @throws BeehiveObjectStore.NotFoundException
     * @throws ClassNotFoundException
     * @throws BeehiveObjectStore.Exception
     * @throws sunlabs.titan.node.BeehiveObjectPool.Exception
     */
    public boolean removeLocalObject(final TitanGuid objectId) throws BeehiveObjectStore.NotFoundException, ClassNotFoundException, BeehiveObjectStore.Exception, sunlabs.titan.node.BeehiveObjectPool.Exception;

    /**
     * Run the given {@link Runnable} on this node's {@link ExecutorService}
     * @param runnable
     * @throws RejectedExecutionException if {@code runnable} cannot be accepted for execution
     */
    public void execute(Runnable runnable) throws RejectedExecutionException;
}
