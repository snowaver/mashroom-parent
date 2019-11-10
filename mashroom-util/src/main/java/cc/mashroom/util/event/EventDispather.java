package cc.mashroom.util.event;

import  java.util.concurrent.CopyOnWriteArrayList;

public  class  EventDispather<L>
{
	protected  CopyOnWriteArrayList<L>  listeners= new  CopyOnWriteArrayList<L>();
	
	public  boolean  addListener(    L  listener )
	{
		return  listener != null && listeners.addIfAbsent( listener );
	}
	
	public  boolean  removeListener( L  listener )
	{
		return  listener != null && this.listeners.remove( listener );
	}
}
