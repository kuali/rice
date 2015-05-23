

##CURRENT
* switching to standard properties
  * Travis Schneberger on Fri, 17 Apr 2015 17:02:03 -0400 [View Commit](../../commit/04d595e166c5da88f123d68ef3f341eba2c998f1)
* switching to standard properties
  * Travis Schneberger on Fri, 17 Apr 2015 17:07:38 -0400 [View Commit](../../commit/59330cd2c3471ee370a733712248e9d85a63c10b)
* back to snapshot
  * Travis Schneberger on Fri, 17 Apr 2015 17:51:28 -0400 [View Commit](../../commit/94bf32f59df81b4177d26be89907a2d190b51cf4)
* removing outdated IDE metadata
  * Travis Schneberger on Thu, 23 Apr 2015 13:06:16 -0400 [View Commit](../../commit/c3691d0f7adf0dcdc9a01201ce36cb6849d38924)
* Update rice pom after release
  * Gayathri on Mon, 27 Apr 2015 16:24:00 -0700 [View Commit](../../commit/a053f0846a6643b5ac9a790e57f64855d3130601)
*  Fixing exception while exporting results.
  * Rice added an enhancement to check permissions before displaying the options to export search results. Rice assumes that the export functionality is only used in lookupForms which have a boEntry but KC uses it in other forms as well. Therefore, when the boEntry is null, I bypass the perm check. I could not override the helper because it is not spring injected, therefore this solution. Additionally, in the absence of a boEntry, how would you check perms? Hence this solution.
  * Details
  * --------
  * Steps to reproduce -
  * 1. From the Home Page - Navigate to Current and Pending Reports Menu Option
  * 2. Search for Select "cate" as PI or any PI that will return results
  * 3. select "current"
  * 4. select initiate report
  * 5. Get results on screen
  * 6. select option to export into spreadsheet, CSV, or XML file
  * Get HTTP 500 error
  * HTTP Status 500 - javax.servlet.ServletException: javax.servlet.jsp.JspException: javax.servlet.jsp.JspException: java.lang.NullPointerException
  * ________________________________________
  * type Exception report
  * message javax.servlet.ServletException: javax.servlet.jsp.JspException: javax.servlet.jsp.JspException: java.lang.NullPointerException
  * description The server encountered an internal error that prevented it from fulfilling this request.
  * exception
  * org.apache.jasper.JasperException: javax.servlet.ServletException: javax.servlet.jsp.JspException: javax.servlet.jsp.JspException: java.lang.NullPointerException org.apache.jasper.servlet.JspServletWrapper.handleJspException(JspServletWrapper.java:549) org.apache.jasper.servlet.JspServletWrapper.service(JspServletWrapper.java:455) org.apache.jasper.servlet.JspServlet.serviceJspFile(JspServlet.java:390) org.apache.jasper.servlet.JspServlet.service(JspServlet.java:334) javax.servlet.http.HttpServlet.service(HttpServlet.java:728) org.apache.struts.action.RequestProcessor.doForward(RequestProcessor.java:1083) org.apache.struts.action.RequestProcessor.processForwardConfig(RequestProcessor.java:396) org.kuali.rice.kns.web.struts.action.KualiRequestProcessor.processFormActionAndForward(KualiRequestProcessor.java:243) org.kuali.rice.kns.web.struts.action.KualiRequestProcessor.strutsProcess(KualiRequestProcessor.java:222) org.kuali.rice.kns.web.struts.action.KualiRequestProcessor.process(KualiRequestProcessor.java:101) org.apache.struts.action.ActionServlet.process(ActionServlet.java:1913) org.kuali.rice.kns.web.struts.action.KualiActionServlet.process(KualiActionServlet.java:202) org.apache.struts.action.ActionServlet.doGet(ActionServlet.java:449) javax.servlet.http.HttpServlet.service(HttpServlet.java:621) javax.servlet.http.HttpServlet.service(HttpServlet.java:728) org.kuali.coeus.sys.framework.controller.interceptor.RequestLoggingFilter.doFilter(RequestLoggingFilter.java:92) org.kuali.rice.kew.web.UserPreferencesFilter.doFilter(UserPreferencesFilter.java:78) org.kuali.rice.kew.web.UserPreferencesFilter.doFilter(UserPreferencesFilter.java:62) org.kuali.rice.krad.web.filter
  * Gayathri on Mon, 27 Apr 2015 17:34:47 -0700 [View Commit](../../commit/7cf2efdb81dd79916e27fdb147c61158d59d52b5)
* Preparing code and libraries to switch to Java 8
  * Travis Schneberger on Fri, 8 May 2015 09:56:51 -0400 [View Commit](../../commit/ac9e1e14f6b6504003dc02bdd8c93adf525aa607)
*  Fix !=null comparison on prepositions.
  * Steps to Recreate
  * 1 Create a People Flow
  * 2 Create an Agenda with a single single proposition. Activity Type != null. I did this because I wanted the rule to always fire no matter what, and as Activity Type is required to save a prop dev doc this field cannot be null.
  * 3 Link the created people flow with his rule so it will populate the route log when this rule executes to true.
  * When I did this it did not populate the route log. Gayathri asked me to try this with a specific Activity Type value, and when I had the rule check for activity type of 1 it properly populated the route log. So KRMS propositions are not currently checking for not null properly.
  * Gayathri on Wed, 13 May 2015 10:22:51 -0700 [View Commit](../../commit/e40f8389dec88fe9e2703105ed302203ab04c4f3)
* move to java 8
  * Travis Schneberger on Thu, 14 May 2015 07:52:29 -0400 [View Commit](../../commit/5f2dcf22328d6bf8532eca08e7150bd70cc88239)
* fixing java 8 generic inference compilation issue.
  * Travis Schneberger on Thu, 14 May 2015 10:57:29 -0400 [View Commit](../../commit/7148d3b52dedf0a0f03823436f435c829948a0cc)
* fixing java 8 generic inference compilation issue.
  * Travis Schneberger on Thu, 14 May 2015 11:30:18 -0400 [View Commit](../../commit/149380ff4e4b53a4d673472d57e50c36541d1330)
*  Fix test. The test was wrong earlier.
  * Gayathri on Thu, 14 May 2015 11:45:34 -0700 [View Commit](../../commit/ef77e4c09f46a06b20d91c1dd032900310a90648)
* turning off lint for javadoc because of Java 8's strict javadoc compiler.
  * Travis Schneberger on Thu, 14 May 2015 17:10:57 -0400 [View Commit](../../commit/f3fdfe8900a53686b2355d89000de4701ff93d92)
* turning off lint for javadoc because of Java 8's strict javadoc compiler.
  * Travis Schneberger on Sun, 17 May 2015 21:26:34 -0400 [View Commit](../../commit/f01c8978571c95b60b3244628ecbb0dccb1c4015)
* [RESKC-432] Pessimistic lock release on log out or session expiration fix
  * bsmith83 on Mon, 18 May 2015 14:40:41 -0700 [View Commit](../../commit/851de7a3e60c94af7a6659dfb3550b3994f0db53)
* [KULRICE-12991] Fix for startup slowdown, flag added to turn on bean instantiation at startup
  * bsmith83 on Tue, 19 May 2015 09:00:16 -0700 [View Commit](../../commit/87c584b8c2ebe86fa79318756719bcefa01a44e5)
* [KULRICE-14248] Security fix
  * bsmith83 on Thu, 21 May 2015 12:42:23 -0700 [View Commit](../../commit/fe0da05a5086b598dcbbdb5480264e5896ab0426)
* [RESKC-452] Allow for unmarshalling of jdk8 types by xstream
  * bsmith83 on Thu, 21 May 2015 14:02:20 -0700 [View Commit](../../commit/a1d7afbea02065f38be54258a90df7a53bb1b71c)
* catching Error in specific cases to support java 7 and java 8
  * Travis Schneberger on Fri, 22 May 2015 07:06:07 -0400 [View Commit](../../commit/5728ac97399dfa23f247c90224943d94d71787ca)

##rice-2.5.4.6-kckualico
* No Changes


##rice-2.5.4.5-kckualico
* update pom to point to new kualico repo location
  * blackcathacker on Thu, 16 Apr 2015 09:26:27 -0700 [View Commit](../../commit/180509ac282ccb118a11b8a61350ba7a069be0ee)
* add grm profile
  * Travis Schneberger on Thu, 16 Apr 2015 16:50:14 -0400 [View Commit](../../commit/1cc22b01950082f12a61618f53d8307e2ce6db1f)
* add grm profile
  * Travis Schneberger on Thu, 16 Apr 2015 16:52:56 -0400 [View Commit](../../commit/d21acc1cca86e748e22d9ceebee8b487276da166)

##rice-2.5.4.4-kckualico
* fixed agenda editor to save term paramters
  * Joe Williams on Wed, 15 Apr 2015 16:24:01 -0500 [View Commit](../../commit/fe575b10e05dfacc4296f8ea4f9e6bf2d1f81978)

##rice-2.5.4.3-kckualico
* Removing IDE files from tracking and adding them to gitignore
  * blackcathacker on Tue, 14 Apr 2015 14:58:03 -0700 [View Commit](../../commit/49a04428c42269f5cd052c80817074be4e2dd96c)
* Do not serialize AgendaBo.ContextBo due to additional agendas

  * When attempting to serialize the contextBo off the agenda in the maint doc the context has links to more all agendas in the context and these are serialized as well which ends up frequently causing an exception due to un-materialized lists.
  * blackcathacker on Tue, 14 Apr 2015 14:59:16 -0700 [View Commit](../../commit/7bd6a6ed866fcf8dfe2a10a252b95f9f3c3219e6)

##rice-2.5.4.2-kckualico
* Need to set the ID on TermBo's TermSpecificationBo, or else a new ID will be incorrectly assigned when the TermBo is persisted.
  * rojlarge on Tue, 7 Apr 2015 12:05:04 -0400 [View Commit](../../commit/cba19ce67fc5f92d7cf129a58aa4b3315214f65f)

##rice-2.5.4.1-kckualico
*  Fixing context serialization
  * Gayathri on Thu, 9 Apr 2015 15:03:09 -0700 [View Commit](../../commit/ac7f7f10d76d54315eede9c3ceca0857db2563d7)

##rice-2.5.4.0-kckualico
* No Changes


##rice-2.5.3.1505.12-kckualico
* catching Error in specific cases to support java 7 and java 8
  * Travis Schneberger on Fri, 22 May 2015 07:06:07 -0400 [View Commit](../../commit/5728ac97399dfa23f247c90224943d94d71787ca)

##rice-2.5.3.1505.11-kckualico
* No Changes


##rice-2.5.3.1505.10-kckualico
* [KULRICE-14248] Security fix
  * bsmith83 on Thu, 21 May 2015 12:42:23 -0700 [View Commit](../../commit/fe0da05a5086b598dcbbdb5480264e5896ab0426)
* [RESKC-452] Allow for unmarshalling of jdk8 types by xstream
  * bsmith83 on Thu, 21 May 2015 14:02:20 -0700 [View Commit](../../commit/a1d7afbea02065f38be54258a90df7a53bb1b71c)

##rice-2.5.3.1505.9-kckualico
* [RESKC-432] Pessimistic lock release on log out or session expiration fix
  * bsmith83 on Mon, 18 May 2015 14:40:41 -0700 [View Commit](../../commit/851de7a3e60c94af7a6659dfb3550b3994f0db53)

##rice-2.5.3.1505.8-kckualico
* No Changes


##rice-2.5.3.1505.7-kckualico
* [KULRICE-12991] Fix for startup slowdown, flag added to turn on bean instantiation at startup
  * bsmith83 on Tue, 19 May 2015 09:00:16 -0700 [View Commit](../../commit/87c584b8c2ebe86fa79318756719bcefa01a44e5)

##rice-2.5.3.1505.6-kckualico
* No Changes


##rice-2.5.3.1505.5-kckualico
* turning off lint for javadoc because of Java 8's strict javadoc compiler.
  * Travis Schneberger on Sun, 17 May 2015 21:26:34 -0400 [View Commit](../../commit/f01c8978571c95b60b3244628ecbb0dccb1c4015)

##rice-2.5.3.1505.4-kckualico
*  Fix test. The test was wrong earlier.
  * Gayathri on Thu, 14 May 2015 11:45:34 -0700 [View Commit](../../commit/ef77e4c09f46a06b20d91c1dd032900310a90648)
* turning off lint for javadoc because of Java 8's strict javadoc compiler.
  * Travis Schneberger on Thu, 14 May 2015 17:10:57 -0400 [View Commit](../../commit/f3fdfe8900a53686b2355d89000de4701ff93d92)

##rice-2.5.3.1505.3-kckualico
*  Fix !=null comparison on prepositions.
  * Steps to Recreate
  * 1 Create a People Flow
  * 2 Create an Agenda with a single single proposition. Activity Type != null. I did this because I wanted the rule to always fire no matter what, and as Activity Type is required to save a prop dev doc this field cannot be null.
  * 3 Link the created people flow with his rule so it will populate the route log when this rule executes to true.
  * When I did this it did not populate the route log. Gayathri asked me to try this with a specific Activity Type value, and when I had the rule check for activity type of 1 it properly populated the route log. So KRMS propositions are not currently checking for not null properly.
  * Gayathri on Wed, 13 May 2015 10:22:51 -0700 [View Commit](../../commit/e40f8389dec88fe9e2703105ed302203ab04c4f3)
* move to java 8
  * Travis Schneberger on Thu, 14 May 2015 07:52:29 -0400 [View Commit](../../commit/5f2dcf22328d6bf8532eca08e7150bd70cc88239)
* fixing java 8 generic inference compilation issue.
  * Travis Schneberger on Thu, 14 May 2015 10:57:29 -0400 [View Commit](../../commit/7148d3b52dedf0a0f03823436f435c829948a0cc)
* fixing java 8 generic inference compilation issue.
  * Travis Schneberger on Thu, 14 May 2015 11:30:18 -0400 [View Commit](../../commit/149380ff4e4b53a4d673472d57e50c36541d1330)

##rice-2.5.3.1505.2-kckualico
* Preparing code and libraries to switch to Java 8
  * Travis Schneberger on Fri, 8 May 2015 09:56:51 -0400 [View Commit](../../commit/ac9e1e14f6b6504003dc02bdd8c93adf525aa607)

##rice-2.5.3.1505.1-kckualico
* No Changes


##rice-2.5.3.1504.2-kckualico
* removing outdated IDE metadata
  * Travis Schneberger on Thu, 23 Apr 2015 13:06:16 -0400 [View Commit](../../commit/c3691d0f7adf0dcdc9a01201ce36cb6849d38924)
* Update rice pom after release
  * Gayathri on Mon, 27 Apr 2015 16:24:00 -0700 [View Commit](../../commit/a053f0846a6643b5ac9a790e57f64855d3130601)
*  Fixing exception while exporting results.
  * Rice added an enhancement to check permissions before displaying the options to export search results. Rice assumes that the export functionality is only used in lookupForms which have a boEntry but KC uses it in other forms as well. Therefore, when the boEntry is null, I bypass the perm check. I could not override the helper because it is not spring injected, therefore this solution. Additionally, in the absence of a boEntry, how would you check perms? Hence this solution.
  * Details
  * --------
  * Steps to reproduce -
  * 1. From the Home Page - Navigate to Current and Pending Reports Menu Option
  * 2. Search for Select "cate" as PI or any PI that will return results
  * 3. select "current"
  * 4. select initiate report
  * 5. Get results on screen
  * 6. select option to export into spreadsheet, CSV, or XML file
  * Get HTTP 500 error
  * HTTP Status 500 - javax.servlet.ServletException: javax.servlet.jsp.JspException: javax.servlet.jsp.JspException: java.lang.NullPointerException
  * ________________________________________
  * type Exception report
  * message javax.servlet.ServletException: javax.servlet.jsp.JspException: javax.servlet.jsp.JspException: java.lang.NullPointerException
  * description The server encountered an internal error that prevented it from fulfilling this request.
  * exception
  * org.apache.jasper.JasperException: javax.servlet.ServletException: javax.servlet.jsp.JspException: javax.servlet.jsp.JspException: java.lang.NullPointerException org.apache.jasper.servlet.JspServletWrapper.handleJspException(JspServletWrapper.java:549) org.apache.jasper.servlet.JspServletWrapper.service(JspServletWrapper.java:455) org.apache.jasper.servlet.JspServlet.serviceJspFile(JspServlet.java:390) org.apache.jasper.servlet.JspServlet.service(JspServlet.java:334) javax.servlet.http.HttpServlet.service(HttpServlet.java:728) org.apache.struts.action.RequestProcessor.doForward(RequestProcessor.java:1083) org.apache.struts.action.RequestProcessor.processForwardConfig(RequestProcessor.java:396) org.kuali.rice.kns.web.struts.action.KualiRequestProcessor.processFormActionAndForward(KualiRequestProcessor.java:243) org.kuali.rice.kns.web.struts.action.KualiRequestProcessor.strutsProcess(KualiRequestProcessor.java:222) org.kuali.rice.kns.web.struts.action.KualiRequestProcessor.process(KualiRequestProcessor.java:101) org.apache.struts.action.ActionServlet.process(ActionServlet.java:1913) org.kuali.rice.kns.web.struts.action.KualiActionServlet.process(KualiActionServlet.java:202) org.apache.struts.action.ActionServlet.doGet(ActionServlet.java:449) javax.servlet.http.HttpServlet.service(HttpServlet.java:621) javax.servlet.http.HttpServlet.service(HttpServlet.java:728) org.kuali.coeus.sys.framework.controller.interceptor.RequestLoggingFilter.doFilter(RequestLoggingFilter.java:92) org.kuali.rice.kew.web.UserPreferencesFilter.doFilter(UserPreferencesFilter.java:78) org.kuali.rice.kew.web.UserPreferencesFilter.doFilter(UserPreferencesFilter.java:62) org.kuali.rice.krad.web.filter
  * Gayathri on Mon, 27 Apr 2015 17:34:47 -0700 [View Commit](../../commit/7cf2efdb81dd79916e27fdb147c61158d59d52b5)

##rice-2.5.3.1504.1-kckualico
*  Fixing super user approve
  * Gayathri on Wed, 11 Mar 2015 11:12:26 -0700 [View Commit](../../commit/4812286a1c83e51a36e9bca98e092449bbacc7cb)
* next iteration
  * Travis Schneberger on Mon, 16 Mar 2015 14:08:23 -0400 [View Commit](../../commit/15c6f88ce16c3b3d3bd083858ecc82c17deae062)
*  Fix role document
  * Gayathri on Mon, 16 Mar 2015 14:01:55 -0700 [View Commit](../../commit/098f5aba3c678f0fcb60c8e550e4efe39ceb0091)
*  unmodifiable list change
  * Gayathri on Tue, 17 Mar 2015 14:31:24 -0700 [View Commit](../../commit/3da68c31da90ce393a56e6a1c0dbed2b821f6b93)
* fix note delete STE
  * Joe Williams on Wed, 25 Mar 2015 11:12:12 -0500 [View Commit](../../commit/36b67bf75a820a2b41f1e42b1be8a1ee0a0811ee)
* adding error prone compiler, fixing errors
  * Travis Schneberger on Thu, 26 Mar 2015 14:06:00 -0400 [View Commit](../../commit/3507eb5f92b98f2f2e68421418fcc8908745dce0)
* adding error prone compiler, fixing errors
  * Travis Schneberger on Thu, 26 Mar 2015 16:25:05 -0400 [View Commit](../../commit/fb578da4e79ca84d88605b1449b98978e8759419)
* release process
  * Travis Schneberger on Sat, 4 Apr 2015 09:53:07 -0400 [View Commit](../../commit/ae26267ff1f6bb53cff4b09bd0053f731fd1337d)
* release process
  * Travis Schneberger on Sat, 4 Apr 2015 19:47:46 -0400 [View Commit](../../commit/5b71fbd05eb61ef42a4296c2f68fedafbaba6800)
* release process
  * Travis Schneberger on Mon, 6 Apr 2015 08:56:20 -0400 [View Commit](../../commit/4917d93c0555f0876b0bcdfc97cf04747a91c538)
* release process
  * Travis Schneberger on Mon, 6 Apr 2015 09:04:13 -0400 [View Commit](../../commit/1de8547ff719a7fc94a7324264fb2ea0d9d74b2b)
* Need to set the ID on TermBo's TermSpecificationBo, or else a new ID will be incorrectly assigned when the TermBo is persisted.
  * rojlarge on Tue, 7 Apr 2015 12:05:04 -0400 [View Commit](../../commit/cba19ce67fc5f92d7cf129a58aa4b3315214f65f)
*  Fixing context serialization
  * Gayathri on Thu, 9 Apr 2015 15:03:09 -0700 [View Commit](../../commit/ac7f7f10d76d54315eede9c3ceca0857db2563d7)
* Removing IDE files from tracking and adding them to gitignore
  * blackcathacker on Tue, 14 Apr 2015 14:58:03 -0700 [View Commit](../../commit/49a04428c42269f5cd052c80817074be4e2dd96c)
* Do not serialize AgendaBo.ContextBo due to additional agendas

  * When attempting to serialize the contextBo off the agenda in the maint doc the context has links to more all agendas in the context and these are serialized as well which ends up frequently causing an exception due to un-materialized lists.
  * blackcathacker on Tue, 14 Apr 2015 14:59:16 -0700 [View Commit](../../commit/7bd6a6ed866fcf8dfe2a10a252b95f9f3c3219e6)
* fixed agenda editor to save term paramters
  * Joe Williams on Wed, 15 Apr 2015 16:24:01 -0500 [View Commit](../../commit/fe575b10e05dfacc4296f8ea4f9e6bf2d1f81978)
* update pom to point to new kualico repo location
  * blackcathacker on Thu, 16 Apr 2015 09:26:27 -0700 [View Commit](../../commit/180509ac282ccb118a11b8a61350ba7a069be0ee)
* add grm profile
  * Travis Schneberger on Thu, 16 Apr 2015 16:50:14 -0400 [View Commit](../../commit/1cc22b01950082f12a61618f53d8307e2ce6db1f)
* add grm profile
  * Travis Schneberger on Thu, 16 Apr 2015 16:52:56 -0400 [View Commit](../../commit/d21acc1cca86e748e22d9ceebee8b487276da166)
* switching to standard properties
  * Travis Schneberger on Fri, 17 Apr 2015 17:02:03 -0400 [View Commit](../../commit/04d595e166c5da88f123d68ef3f341eba2c998f1)
* switching to standard properties
  * Travis Schneberger on Fri, 17 Apr 2015 17:07:38 -0400 [View Commit](../../commit/59330cd2c3471ee370a733712248e9d85a63c10b)
* back to snapshot
  * Travis Schneberger on Fri, 17 Apr 2015 17:51:28 -0400 [View Commit](../../commit/94bf32f59df81b4177d26be89907a2d190b51cf4)

##rice-2.5.3-kckualico
* Massive search and replace on version numbers to bind to <version>2.5.2.0-kualico-SNAPSHOT</version>
  * Lance Speelmon on Fri, 21 Nov 2014 15:16:05 -0700 [View Commit](../../commit/57078702ee708f506878d508ca46c05aeca16da9)
* Removed portal.css
  * Lance Speelmon on Tue, 11 Feb 2014 16:23:43 -0700 [View Commit](../../commit/6976e11f023e18fbc41f61b0e1fa1e4848914b24)
* Trying to fix issues #114: Caused by: java.lang.NullPointerException
        at org.kuali.rice.kns.web.struts.form.pojo.StrutsExceptionIncidentHandler.execute(StrutsExceptionIncidentHandler.java:83)
        at org.apache.struts.action.RequestProcessor.processException(RequestProcessor.java:530)
        ... 58 more
  * Lance Speelmon on Wed, 12 Feb 2014 14:53:29 -0700 [View Commit](../../commit/7eaa8a86ec8a0b757338b45ad5e6a35803135173)
* Added skinning changes
  * Lance Speelmon on Wed, 12 Feb 2014 15:43:06 -0700 [View Commit](../../commit/1f4163c0452ca2e39084b23f5cab4b54c161b1b5)
* Hackish attempt to get bootstrap-skinned.css included in dummy login page HTML.
  * Maybe this would be better accomplished with a spring override?
  * Lance Speelmon on Mon, 3 Mar 2014 11:46:14 -0700 [View Commit](../../commit/c7070a6f7b4dfef3cc2cb1efd26758afe17d06ea)
* Try to fix Finalizer memory leak related to CGLIB proxies
  * Lance Speelmon on Sat, 15 Mar 2014 14:11:19 -0700 [View Commit](../../commit/3c64e103f079e5953e519156da5ddc4470675eb0)
* Attempt to fix IFRAME resizing issue in 5.2
  * Lance Speelmon on Tue, 18 Mar 2014 14:18:59 -0700 [View Commit](../../commit/dcf0fd735dca21c51a077b631fd1a40787c26f00)
* Revert "Try to fix Finalizer memory leak related to CGLIB proxies"

  * This reverts commit 29dba51b495e25254b9c8b16e4527d56357a3b18.
  * Lance Speelmon on Tue, 15 Apr 2014 10:37:34 -0700 [View Commit](../../commit/a8afeebf802d30792fb764933442bff80c2d3e90)
* Reduce the amount of objects being cached by ehcache

  * Conflicts:
	rice-middleware/krms/impl/src/main/resources/org/kuali/rice/krms/config/krms.ehcache.xml

  * Conflicts:
	rice-middleware/kim/kim-impl/src/main/resources/org/kuali/rice/kim/impl/config/kim.ehcache.xml
  * Lance Speelmon on Fri, 11 Apr 2014 20:09:03 -0700 [View Commit](../../commit/c4e6f4ff38bfd17dbac662398b9f7fa2bf7e0966)
* Specified JDK source and target version for maven-compiler-plugin of java 1.6
  * Lance Speelmon on Thu, 17 Apr 2014 13:32:45 -0700 [View Commit](../../commit/9413007dd6d9966089aa43959ed2bc7413330754)
* Ignore InvocationTargetException when comparing bean properties
  * fixes https://github.com/rSmart/issues/issues/173
  * Lance Speelmon on Tue, 20 May 2014 14:29:49 -0700 [View Commit](../../commit/55e00707340d643f323ce23aae1bc5afe14afc3b)
* Ignore some eclipse IDE files

  * Conflicts:
	.gitignore
	rice-middleware/serviceregistry/.project
	rice-middleware/standalone/.project
	rice-middleware/web/.project
  * Lance Speelmon on Tue, 20 May 2014 14:42:44 -0700 [View Commit](../../commit/8d5419f18f1761fbdb92f072ffa90fb7cd0e7733)
* Ignore NullPointerException when comparing bean properties
  * fixes https://github.com/rSmart/issues/issues/173
  * Lance Speelmon on Tue, 20 May 2014 15:39:23 -0700 [View Commit](../../commit/17af44fc8b8a9b21215a945dc99f25fa4d98f76a)
* Add some debug statements to help with https://github.com/rSmart/issues/issues/184
  * Lance Speelmon on Wed, 21 May 2014 14:25:49 -0700 [View Commit](../../commit/f45a436201d52e67099d2c864611bcb6253bd4f1)
* Add some debug statements to help with https://github.com/rSmart/issues/issues/184
  * Lance Speelmon on Wed, 21 May 2014 14:25:49 -0700 [View Commit](../../commit/d1abcc46e8d3d7c4218f98ca4a8b1b9fcc44afe0)
* Fixes unit test for previous BeanPropertyComparator changes:
  * Commit: d288d095bec7adcc5327f4abb125f01e58ce831c
  * Ignore NullPointerException when comparing bean properties
  * fixes https://github.com/rSmart/issues/issues/173

  * Commit: f86afd95a7a8c2b01ad9979dbd95d06b84e0cbb2
  * Ignore InvocationTargetException when comparing bean properties
  * fixes https://github.com/rSmart/issues/issues/173
  * Lance Speelmon on Thu, 22 May 2014 18:47:54 -0700 [View Commit](../../commit/94bcbfcfee2c9ba93478e3956ee489167e40f623)
* Increase maven heap size to try to work around: Cannot run program "/bin/sh": java.io.IOException: error=12, Cannot allocate memory
  * [ERROR] Failed to execute goal org.apache.maven.plugins:maven-jar-plugin:2.4:test-jar (testjar) on project rice-impl: Error assembling JAR: Failed to retrieve numeric file attributes using: '/bin/sh -c ls -1laR /var/lib/jenkins/jobs/cx_rice/workspace/rice-middleware/impl/target/test-classes': Error while executing process. Cannot run program "/bin/sh": java.io.IOException: error=12, Cannot allocate memory -> [Help 1]

  * Conflicts:
	pom.xml
  * Lance Speelmon on Fri, 30 May 2014 15:26:19 -0700 [View Commit](../../commit/e1166dce1b3e98fa567af3a1915442b01a01beae)
* Adding fix for ISSUE-201. There was a bug in countdown for safari browsers. See https://github.com/kbwood/countdown/issues/18. This should fix the problem

  * Conflicts:
	rice-framework/krad-web/src/main/webapp/plugins/countdown/jquery.countdown.js
  * Przybylski 중광 on Thu, 12 Jun 2014 07:45:30 -0700 [View Commit](../../commit/76ed138cbc4898ca72c0ab5765150668c8e82dc1)
* take notes on merging CX fixes
  * Lance Speelmon on Fri, 21 Nov 2014 19:10:32 -0700 [View Commit](../../commit/8e7217a696cd84b1d8610f7f2f11064cfaeb1847)
* updated PORTING doc
  * Lance Speelmon on Fri, 21 Nov 2014 19:12:21 -0700 [View Commit](../../commit/b7e34b7c98803af35514de60b816fd3df6f9ff73)
* Adding fix for rSmart/issues#207. There was a bug in countdown for safari browsers. See https://github.com/kbwood/countdown/issues/18. This should fix the problem

squash! Adding fix for ISSUE-207. There was a bug in countdown for safari browsers. See https://github.com/kbwood/countdown/issues/18. This should fix the problem

  * fixes rSmart/issues#207

  * Conflicts:
	rice-framework/krad-web/src/main/webapp/plugins/countdown/jquery.countdown.js
  * Przybylski 중광 on Thu, 12 Jun 2014 07:45:30 -0700 [View Commit](../../commit/8e26a535c75545b0485ee7075988fad382dce28b)
* ignore printing out byte arrays in toString methods

  * Conflicts:
	rice-middleware/kns/src/main/java/org/kuali/rice/krad/bo/BusinessObjectBase.java
  * Lance Speelmon on Tue, 15 Jul 2014 15:28:33 -0700 [View Commit](../../commit/d65ce1c0b1f3bc2e8a38d7f819b0af97d98b1b76)
* Updating principal name to be consistent with requirements for GSU. Realisticly all email addresses allow all characters,
so it makes sense to allow all email addresses; however, usernames typically don't allow whitespace.

  * fixes rSmart/issues#318
  * Przybylski 중광 on Wed, 23 Jul 2014 10:51:03 -0400 [View Commit](../../commit/5ba746224eeddac20c33f8f727687823083a2d3f)
* Adding fix to maintenance documents for close action. The original portal findforward forced a POST which is unnecessary on close.
  * Przybylski 중광 on Wed, 30 Jul 2014 17:34:23 -0700 [View Commit](../../commit/1bd20957b138ddda426c5d72bf5ef33181c4a4f2)
* Adding callback handling and removal of save button on processed and final states.

  * fixes rSmart/issues#303

  * Conflicts:
	rice-middleware/kns/src/main/java/org/kuali/rice/kns/web/struts/action/KualiDocumentActionBase.java
  * Przybylski 중광 on Fri, 1 Aug 2014 15:49:20 -0700 [View Commit](../../commit/ac422df8877a28466f0097464b6a05807c8a661c)
* updated PORTING doc
  * Lance Speelmon on Sat, 22 Nov 2014 09:05:28 -0700 [View Commit](../../commit/ac3caa16eb24da8481e28303438575d04d99dae2)
* gracefully handle missing Principal from NotificationRecipientServiceKimImpl.isUserRecipientValid
  * Fixes https://github.com/rSmart/issues/issues/377
  * Lance Speelmon on Fri, 8 Aug 2014 06:18:38 -0700 [View Commit](../../commit/40e3de69b5ab0dbbed4aa1f3625aacf4b7e28a6b)
* Revised fix: gracefully handle missing Principal from NotificationRecipientServiceKimImpl.isUserRecipientValid
  * Fixes https://github.com/rSmart/issues/issues/377
  * Lance Speelmon on Mon, 11 Aug 2014 15:21:07 -0700 [View Commit](../../commit/f9dcc5ec027d275be373c51a4bfdb16a327680a1)
* Revised fix: gracefully handle missing Principal from NotificationRecipientServiceKimImpl.isUserRecipientValid
  * Fixes https://github.com/rSmart/issues/issues/377
  * Lance Speelmon on Tue, 12 Aug 2014 09:35:11 -0700 [View Commit](../../commit/1758966f29907af32ae1e24975d6ab41a3b7f70e)
* Adding check for whether the document can actually be saved or not.

  * fixes rSmart/issues#386

  * Conflicts:
	rice-middleware/kns/src/main/java/org/kuali/rice/kns/web/struts/action/KualiDocumentActionBase.java
  * Przybylski 중광 on Tue, 12 Aug 2014 16:48:16 -0700 [View Commit](../../commit/460209f1b5adb8fec02a5f49c5ba60b20d2ea768)
* updated PORTING doc
  * Lance Speelmon on Sat, 22 Nov 2014 09:19:15 -0700 [View Commit](../../commit/f536fbd04c8a066b12577c05aa6605a480853e9d)
* Principal Name is actually Principal Id for some of our clients. We need to check this as both.

  * Adding new configuration property to determine whether to allow login with principalId or principalName.

  * fixes rSmart/issues#420
  * Przybylski 중광 on Mon, 8 Sep 2014 17:56:03 -0400 [View Commit](../../commit/001219ee662dd05bfd0051e407cfae8dec5dc734)
* Principal Name is actually Principal Id for some of our clients
  * Adding new configuration property to determine whether to allow login with principalId or principalName.
  * fixes rSmart/issues#420
  * Lance Speelmon on Tue, 9 Sep 2014 14:19:08 -0700 [View Commit](../../commit/737ac9e36a4da48cf51f7656729a81e0d3e911b6)
* java.lang.NullPointerException
   org.apache.commons.beanutils.PropertyUtilsBean.getIndexedProperty(PropertyUtilsBean.java:507)
   org.kuali.rice.kns.web.struts.form.pojo.PojoPropertyUtilsBean.getIndexedProperty(PojoPropertyUtilsBean.java:267)
  * Lance Speelmon on Wed, 10 Sep 2014 16:05:48 -0700 [View Commit](../../commit/051ce90b13c2f65a5ce4620d052d0c6797cbffd0)
* FIXME disable PersonLookupableImplTest.testGetCreateNewUrl as a workaround
  * Lance Speelmon on Wed, 10 Sep 2014 17:08:18 -0700 [View Commit](../../commit/1b147c65e9dc93fe2260db9f0c691e709f17600a)
* java.lang.IllegalArgumentException: Failed to locate a principal with principal name 'kr'
  * https://github.com/rSmart/issues/issues/435
  * Lance Speelmon on Tue, 16 Sep 2014 20:25:32 -0700 [View Commit](../../commit/dcf207c80b4e267fd47d0a8b016907b7a70c94c5)
* add some additonal debugging to help with https://github.com/rSmart/issues/issues/500
  * Lance Speelmon on Wed, 12 Nov 2014 09:44:52 -0500 [View Commit](../../commit/2fd5385c8f796f3abd6a38a65747c0d4ae54147c)
* updated CX_PORTING.md
  * Lance Speelmon on Mon, 24 Nov 2014 09:26:37 -0700 [View Commit](../../commit/e84c6c2ffa67e4c0b583d35470532ac2f8ab1346)
* KRACOEUS-8824 : Rice changes to fix auto-ingestion
  * blackcathacker on Fri, 13 Feb 2015 15:50:43 -0800 [View Commit](../../commit/705f1788f3728a09202ca6d0be6c0c7f0f01e01f)
* KRACOEUS-8845: Adding ScaleTwo and ScaleThree decimal support
  * Gayathri on Mon, 16 Feb 2015 16:27:41 -0700 [View Commit](../../commit/35b04388b8e096dff7df8675ef2e05a85e81c565)
* KRACOEUS-8848: Fixing super user annotations
  * Gayathri on Mon, 23 Feb 2015 12:46:13 -0700 [View Commit](../../commit/985d8c2581a6c25a02b0417e5dd2f0305a8bf75f)
* KRACOEUS-8915:Treat JPA IndirectList like ArrayList for serialization
  * During KEW serialization of the document for document content, PD was serializing JPAs indirectlist proxy instead of the actual list and contents.
  * blackcathacker on Thu, 26 Feb 2015 14:38:10 -0800 [View Commit](../../commit/9f5d5e6e04b6a993e51db42864ebfa2f294a45b0)
* KRACOEUS-8803:Fix role search when user is not a member of groups
  * Due to the old organization of the code if the roleIds or groupIds was empty there would be a Predicate that essentially said include all roles or all groups. This organizes the code so it will only include those predicates if there is something to compare to.
  * blackcathacker on Fri, 27 Feb 2015 17:56:03 -0800 [View Commit](../../commit/5ce12317d40ad88f3e1e48d819e3014d6f7a2a0b)
* KRACOEUS-8928: upgrading wss4j
  * Travis Schneberger on Wed, 4 Mar 2015 16:02:20 -0500 [View Commit](../../commit/aeb65489906cbdbc9572a0084867bdc34f70100f)
* Resolve issue with default xstream implementations
  * Setting a default implementation for a class, also overrides the inverse in xstream so removing default implementations as we define a custom implementation for lazy loaded lists
  * And handling JPA Indirect Lists similarly to OJB Lazy lists
  * blackcathacker on Wed, 11 Mar 2015 11:43:16 -0700 [View Commit](../../commit/15734e81fde820e0f32a84e46c03d4a9e72ca0cd)
* Use custom mapper to fix peristence mappings
  * Instead of trying to use default mappings and custom converters, use custom mapper so xstream will seriazlie and deserialize all proxied/managed lists as real lists.
  * blackcathacker on Wed, 11 Mar 2015 15:33:05 -0700 [View Commit](../../commit/53c0eddd6b2e137a7e3ad09740ec98e4e0acd1b4)
* releasing
  * Travis Schneberger on Mon, 16 Mar 2015 14:04:20 -0400 [View Commit](../../commit/bb24347540ccf33aa9289ee4bcace5ab1f28dd81)