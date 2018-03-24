

package com.zgm.zlib.directions.model;

//import org.parceler.Parcel;


//@Parcel(parcelsIndex = false)
public class Bound {
    Coordination northeast;
    Coordination southwest;

    public Coordination getNortheastCoordination() {
        return northeast;
    }

    public Coordination getSouthwestCoordination() {
        return southwest;
    }
}
