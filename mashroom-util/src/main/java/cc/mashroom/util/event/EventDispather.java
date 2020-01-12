package cc.mashroom.util.event;

import java.util.Arrays;
import java.util.List;
import  java.util.concurrent.CopyOnWriteArrayList;

public  class  EventDispather  <L>
{
	protected  CopyOnWriteArrayList<L>   listeners = new  CopyOnWriteArrayList<L>();
	
	public  void  addListeners(    L...  listeners )
	{
		this.listeners.addAllAbsent( Arrays.asList(listeners) );
	}
	
	public  void  clearListeners()
	{
		listeners.clear();
	}
	
	public  void  removeListeners( L...  listeners )
	{
		this.listeners.removeAll(    Arrays.asList(listeners) );
	}
	
	public  List<L> getListeners()
	{
		return  new  CopyOnWriteArrayList<L>(  this.listeners );
	}
}
