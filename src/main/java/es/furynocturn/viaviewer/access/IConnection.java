package es.furynocturn.viaviewer.access;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

/** Version del servidor a traducir en esta conexion (null = vanilla, sin Via). */
public interface IConnection {

    ProtocolVersion fnviaviewer$getTarget();

    void fnviaviewer$setTarget(ProtocolVersion target);
}
