package pubnative;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class CreateNewApp {
	
	public WebDriver driver;
	private String platformType;
	private String appIdOrPackage;
	private String appNickname;
	private String category;
	private String keywords;
	private String username;
	private String password;
	
	String PATH = null;
	String API_TOKEN = null;
	
	//Property Files
	private String appPropertiesFile = "\\app.propperties";
	private String requestParamPropFile = "\\requestParam.properties";
	
	private String URL = "https://dashboard.pubnative.net"; 
	
	// LOCATORS
	private String email_locator = "//div[@id='signin']//input[@id='email']";
	private String password_locator = "//div[@id='signin']//input[@id='password']";
	private String login_button_locator = "//input[@value='LOGIN']";
	private String login_success_locator = "//div[contains(@class,'alertMessage') and contains(text(),'Logged in successfully')]";
	private String apps_menu_locator = "//a[@href='/apps']";
	private String new_app_button_locator= "//a[@href='/apps/new']";
	private String platform_locator= "//div[@id='platform-selector']//select[@id='app_platform_id']";
	private String appstore_locator = "app_store_application_id";
	private String nickname_locator = "app_title";
	private String category_locator = "//div[@id='app-main_category_id-group']//select[@id='app_main_category_id']/option[text()='";
	private String keyword_locator = "app_keywords";
	private String finish_button_locator = "//input[@value='Finish' and @id='new-app-submit']";
	private String api_token_locator = "//textarea[@id='copy-token-field']";
	
	// JAVASCRIPTS
	private String scroll_into_view_js = "arguments[0].scrollIntoView(true);";
	private String platform_select_js = "document.getElementById('app_platform_id').selectedIndex='";
	private String category_select_js = "document.getElementById('app_main_category_id').selectedIndex='";
	
	@BeforeTest
	public void setUp(){
		
		FileInputStream fileInput =null;
		
		try {
			
			Path currentRelativePath = Paths.get("");
			PATH = currentRelativePath.toAbsolutePath().toString();
			System.out.println("Getting the App properties from the Properties file");
			File file = new File(PATH + appPropertiesFile);
			fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInput);
			
			platformType = properties.getProperty("platform");
			appIdOrPackage = properties.getProperty("appIdOrPackage");
			appNickname = properties.getProperty("appNickname");
			category = properties.getProperty("category");
			keywords = properties.getProperty("keywords");
			username = properties.getProperty("username");
			password = properties.getProperty("password");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileInput != null) {
				try {
					fileInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("Setting the path of gecko driver");
		System.setProperty("webdriver.gecko.driver", PATH + "\\geckodriver\\geckodriver.exe");

		System.out.println("Initializing Firefox Browser");
		driver = new FirefoxDriver();
		
		System.out.println("Maximizing Window");
		driver.manage().window().maximize();
		
		System.out.println("Setting implicit wait of 10 seconds");
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
	}
	
	
	@Test
	public void createNewApp() throws InterruptedException{
		
		System.out.println("Browsing Pubnative Website");
		driver.get(URL);
		
		System.out.println("Entering username and password");
		driver.findElement(By.xpath(email_locator)).sendKeys(username);
		driver.findElement(By.xpath(password_locator)).sendKeys(password);
		
		System.out.println("Clicking on Login button");
		driver.findElement(By.xpath(login_button_locator)).submit();
		
		System.out.println("Waiting for Login to be successfull and Login sussess message to be appear on dashboard");
		waitForElementToPresent(login_success_locator);
		
		System.out.println("Waiting for App Menu in Header to be clickable");
		waitForElementToBeClickable(apps_menu_locator, 10);
		
		System.out.println("Clicking on Apps Menu in header");
		driver.findElement(By.xpath(apps_menu_locator)).click();
		
		System.out.println("Clicking on App Menu again if last click failed to open");
		if((driver.findElements(By.xpath(new_app_button_locator)) == null)){
			driver.findElement(By.xpath(apps_menu_locator)).click();
			waitForElementToBeClickable(new_app_button_locator, 10);
		}
		
		System.out.println("Clicking on the New App button");
		driver.findElement(By.xpath(new_app_button_locator)).click();
		
		System.out.println("Clicking on the New App button if last click failed");
		if(driver.findElement(By.xpath(new_app_button_locator)) != null)
			driver.findElement(By.xpath(new_app_button_locator)).click();
		
		System.out.println("Waiting for New App form to appear");
		waitForElementToPresent(platform_locator);
		
		System.out.println("Scrolling down");
		((JavascriptExecutor) driver).executeScript(scroll_into_view_js, driver.findElement(By.xpath(platform_locator)));

		System.out.println("Running Javascript to select the platform type");
		if(platformType.equals("ios"))
		    ((JavascriptExecutor) driver).executeScript(platform_select_js + "0'");
		else
			((JavascriptExecutor) driver).executeScript(platform_select_js + "1'");
		
		System.out.println("Entering Other Details - AppID or Package name and App Name");
		driver.findElement(By.id(appstore_locator)).sendKeys(appIdOrPackage);
		driver.findElement(By.id(nickname_locator)).sendKeys(appNickname);
		
		System.out.println("Getting the index number for the Category to be selected");
		String valueOfOption = getAttributeValue(category_locator + category + "']", "value");
		
		System.out.println("Running Javascript to choose the category specified in the properties file");
		((JavascriptExecutor) driver).executeScript(category_select_js + valueOfOption + "'");
		
		System.out.println("Entering Keywords");
		driver.findElement(By.id(keyword_locator)).sendKeys(keywords);
		
		System.out.println("Clicking on the Finish Button");
		driver.findElement(By.xpath(finish_button_locator)).click();
		
		System.out.println("Waiting for API Token to appear");
		waitForElementToPresent(api_token_locator);
		
		System.out.println("Getting API Token");
		API_TOKEN = driver.findElement(By.xpath(api_token_locator)).getText();
		Assert.assertTrue(!API_TOKEN.equals(null), "Veirfying that API token is successfully generated");
		System.out.println("API Token is " + API_TOKEN);
		
	}
	
	@AfterTest
	public void writeApiTokenToFile() {
		
		System.out.println("Writing API Token to request params file");
		Properties props = new Properties();
		FileInputStream input = null;
		FileOutputStream out = null;
		File file = null;

		try {

			file = new File(PATH + requestParamPropFile);
			
			input = new FileInputStream(file);
			props.load(input);
			
			out = new FileOutputStream(file);
			System.out.println("Writing the API Token");
			props.setProperty("apiToken", API_TOKEN);
			props.store(out, null);
			
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (input != null && out != null) {
				try {
					input.close();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String getAttributeValue(String locator, String attribute){
		System.out.println("Returning Attribute value of the node");
		return driver.findElement(By.xpath(locator)).getAttribute(attribute);
		
	}
	
	public void waitForElementToPresent(String locator){
		System.out.println("Wait for element to be present");
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(locator)));
		
	}
	
    public void waitForElementToBeClickable(String locator, long time){
    	System.out.println("Wait for element to be clickable");
		WebDriverWait wait = new WebDriverWait(driver, time);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(locator)));
		
	}
	
}


