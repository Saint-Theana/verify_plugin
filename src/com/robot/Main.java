package com.robot;
import java.util.*;
import com.Tick_Tock.ANDROIDQQ.sdk.*;
import java.util.regex.*;
import org.json.*;


 





public class Main implements Plugin
{
	
	public class st{
		public long group_uin;
		public long uin;
		public String code;
	}

	private List<st> cache = new ArrayList<st>();
	
	private API api;

	@Override
	public String Version()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public String author()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public String name()
	{
		return "测试机器人";
	}
	
	
	
	
	@Override
	public void onLoad(API p1)
	{
		this.api = p1;
	}

	
	
	
	
	@Override
	public void onMessageHandler(GroupMessage qqmessage)
	{
		MessageFactory factory = new MessageFactory();
		factory.message_type=0;
		factory.group_uin=qqmessage.group_uin;
		if(qqmessage.message.matches("[a-zA-Z0-9]*")){
			Main.st yy = null;
			for(st t : this.cache){
				if(t.group_uin==qqmessage.group_uin&&t.uin==qqmessage.sender_uin){
					if(t.code.equals(qqmessage.message)){
						factory.message="验证通过";
						yy=t;
					}else{
						factory.message="验证失败";
					}
					this.api.SendGroupMessage(factory);
					break;
				}
			}
			if(yy!=null){
			
			this.cache.remove(yy);
			}
			
		}
		
	}
	
	
	@Override
	public void onMessageHandler(FriendMessage qqmessage)
	{
		
	}

	@Override
	public void onMessageHandler(TempolaryMessage qqmessage)
	{
		
	}

	@Override
	public void onMessageHandler(final RequestMessage qqmessage)
	{
		MessageFactory factory = new MessageFactory();
		factory.group_uin=qqmessage.group_uin;
		if(qqmessage.ispassed){
			String code = Util.getRandomString(4);
			st t= new st();
			t.code=code;
			t.uin=qqmessage.requestor;
			t.group_uin=qqmessage.group_uin;
			this.cache.add(t);
			AtStore store = new AtStore();
			store.at_name="新成员";
			store.target_uin=qqmessage.requestor;
			factory.at_list.add(store);
			factory.message="验证系统，请在1分钟内输入验证码: "+code+" 否则将会被踢出。";
			this.api.SendGroupMessage(factory);
			new Thread(){
				@Override public void run(){
					try
					{
						this.sleep(60000);
					}
					catch (InterruptedException e)
					{e.printStackTrace();}
					Main.st yy = null;
					if(Main.this.cache.size()!=0){
						for(st t : Main.this.cache){
							if(t.group_uin==qqmessage.group_uin&&t.uin==qqmessage.requestor){
								yy=t;
								MessageFactory factory = new MessageFactory();
								factory.group_uin=qqmessage.group_uin;
								factory.target_uin=qqmessage.requestor;
								Main.this.api.GroupMemberDelete(factory);
								break;
							}
						}
						if(yy!=null){

							Main.this.cache.remove(yy);
						}
					}
				}
			}.start();
			return;
		}
		factory = new MessageFactory();
		factory.requestid=qqmessage.request_id;
		factory.ispass=true;
		factory.group_uin=qqmessage.group_uin;
		factory.target_uin=qqmessage.requestor;
		this.api.DealGroupRequest(factory);
		
		
	}

	
	
	
	
	
	
}
