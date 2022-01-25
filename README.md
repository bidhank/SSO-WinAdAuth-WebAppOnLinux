# SSO-WinAdAuth-WebAppOnLinux
This is a springboot app that provides SSO functionality. It authenticates with Windows Active Directory Server. Webapp (Tomcat) is hosted on Linux (CentOs).

Please follow these steps carefully




					---------------------------------------------------- SSO/AD Windows , Tomcat on Linux ------------------------------------------------------------------
					
					- Create a user(tomcat) in AD Windows (Uncheck "change password after 1st login")
					- Excecute the command 
						c:\> setspn -A HTTP/applicationhost@YOURDOMAIN.COM tomcat  (Service Principal Name = SPN)
						-> tomcat = user that was created in AD windows (Just say this is a Service Name)
						-> applicationhost = The host that is going to have a tomcat-server
						-> YOURDOMAIN = Ad Windows Domain
							
					- We have to assign  applicationhost to Ip Address in AD Windows by editing C:\Windows\System32\drivers\etc\hosts
					- Similarly, we have to assign AD Windows hostname to Ip Address by editing /etc/hosts
					- Generate kerberos keys by executing the following command in Ad Windows
							c:\> ktpass /out c:\tomcat.keytab /mapuser tomcat@YOURDOMAIN.COM /princ 
							HTTP/applicationhost@YOURDOMAIN.COM /pass TomcatPassword /ptype KRB5_NT_PRINCIPAL /crypto All
					 		-> tomcat@YOURDOMAIN.COM is the user we've created at the very first step.
							-> /pass must match the user's password which was created in AD windows
					- When command is executed successfully, keytab file is generated in c:\>tomcat.keytab
					- Copy that tomcat.keytab file into linux 
					- Install a package that allows to log in to AD windows using Kerberos: yum install cyrus-sasl-gssapi
					- Domain Client(Computer who wants to access webapp/service) must add "http://applicationhost:8080" (webapp/service which is running on Tomcat/Linux) in 
						Internet Explorer ->
											Tools ->
													Internet Options ->
																		Local intranet ->
																							Sites  
					- Now, above step will prevent users from having to re-enter login credentials, which is a key piece to this solution.
					- Just in case, make sure "Automatic logon only in Intranet zone" is ticked. It is ticked by default.
					- So the setting we have done in Internet Explorer, Google Chrome will auto implement that setting. No need to tweak.
					- Now just fire up the Chrome and put hostname:port , don't use IP address it will not work.
					- Now you are good to go.
					
					
					
					
					
					
