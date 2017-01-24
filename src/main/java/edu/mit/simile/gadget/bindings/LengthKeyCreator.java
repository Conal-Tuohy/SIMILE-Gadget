package edu.mit.simile.gadget.bindings;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.SecondaryKeyCreator;

import edu.mit.simile.gadget.data.Value;

public class LengthKeyCreator implements SecondaryKeyCreator {
    
    EntryBinding valueBinder = Value.getBinding();
    EntryBinding intBinder = TupleBinding.getPrimitiveBinding(Integer.class);
    
    public boolean createSecondaryKey(SecondaryDatabase secDb, DatabaseEntry keyEntry, DatabaseEntry dataEntry, DatabaseEntry resultEntry) {
        Value v = (Value) valueBinder.entryToObject(dataEntry);
        intBinder.objectToEntry(new Integer(v.getValue().length()), resultEntry);
        return true;
    }
} 
