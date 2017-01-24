package edu.mit.simile.gadget.bindings;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.SecondaryKeyCreator;

import edu.mit.simile.gadget.data.Value;
import edu.mit.simile.gadget.utils.StringUtils;

public class ClusteringKeyCreator implements SecondaryKeyCreator {
    
    EntryBinding valueBinder = Value.getBinding();
    EntryBinding stringBinder = TupleBinding.getPrimitiveBinding(String.class);
    
    public boolean createSecondaryKey(SecondaryDatabase secDb, DatabaseEntry keyEntry, DatabaseEntry dataEntry, DatabaseEntry resultEntry) {
        Value v = (Value) valueBinder.entryToObject(dataEntry);
        stringBinder.objectToEntry(StringUtils.keyfy(v.getValue()), resultEntry);
        return true;
    }
} 
