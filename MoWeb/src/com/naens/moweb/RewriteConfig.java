package com.naens.moweb;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.rule.Join;

@RewriteConfiguration
public class RewriteConfig extends HttpConfigurationProvider {
	@Override
	public int priority() {
		return 10;
	}

	@Override
	public Configuration getConfiguration(final ServletContext context) {
		return ConfigurationBuilder.begin()

		          // A basic join
		          .addRule(Join.path("/").to("/WEB-INF/content/index.xhtml"))

		          // Using parameters to return physical resources
		          .addRule(Join.path("/{param}.html").to("/WEB-INF/content/{param}.xhtml"))

		          // Using parameterization (the value of 'p' is converted to a request parameter)
//		          .addRule(Join.path("/project/{p}").to("/pages/project/create.xhtml"))

		          // Redirect requests to the server-side resource to the correct location
//		          .addRule(Join.path("/signup").to("/pages/signup.xhtml").withInboundCorrection())
		        		  
//        		  .addRule().when(Direction.isInbound().and(Path.matches("/some/{page}/"))).perform(Forward.to("/new-{page}/"))
        		  ;
	}

	/*
	 * 
		<pattern value="/X.html" />
		<view-id value="/WEB-INF/content/X.xhtml" />

		<pattern value="/" />
		<view-id value="/WEB-INF/content/index.xhtml" />
	 * 
	 */
}
