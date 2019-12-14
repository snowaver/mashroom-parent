package cc.mashroom.util.event;

import java.util.List;
import  java.util.concurrent.CopyOnWriteArrayList;

public  class  EventDispather  <L>
{
	protected  CopyOnWriteArrayList<L>  listeners= new  CopyOnWriteArrayList<L>();
	
	public  boolean  addListener(    L  listener )
	{
		return  listener != null && listeners.addIfAbsent( listener );
	}
		
	public  void  clearListeners()
	{
		listeners.clear();
	}
	
	public  boolean  removeListener( L  listener )
	{
		return  listener != null && this.listeners.remove( listener );
	}
	
	public  List<L> getListeners()
	{
		return  new  CopyOnWriteArrayList<L>( this.listeners );
	}
}
