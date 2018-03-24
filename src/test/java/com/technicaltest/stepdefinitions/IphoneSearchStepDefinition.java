package com.technicaltest.stepdefinitions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import junit.framework.Assert;

public class IphoneSearchStepDefinition {

	WebDriver driver;
	JavascriptExecutor jse;
	Actions myAction;
	WebDriverWait myWait;
	List<WebElement> storageList;
	Long highestMemory = 0L;
	WebElement highestMemoryElement = null;
	Long memory;
	Pattern p;
	Matcher m;
	String modelName = "";
	String modelPrice = "";
	String amazonUrl = "";
	String previousUrl = "";

	@Given("^I navigate to google website$")
	public void i_navigate_to_google_website() {
		System.setProperty("webdriver.chrome.driver", "C:\\Himaja\\chromedriver_win32\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS); // to achieve page synchronisation
		driver.manage().timeouts().pageLoadTimeout(45, TimeUnit.SECONDS);
		driver.get("http://www.google.co.uk");
	}

	@When("^I search iphone$")
	public void i_search_iphone() {
		driver.findElement(By.id("lst-ib")).sendKeys("iPhone");
		driver.findElement(By.name("btnK")).submit();
	}

	@Then("^I see list of suggestions$")
	public void i_see_list_of_suggestions() {
		String title = driver.getTitle();
		title.equals("iPhone - Google Search");
	}

	@When("^I search for amazon in suggestions$")
	public void i_search_for_amazon_in_suggestions() {
		String pageSource = "";
		myAction = new Actions(driver);
		boolean status = true;
		myWait = new WebDriverWait(driver, 20);
		while (status) {
			pageSource = driver.getPageSource();
			if (pageSource.contains("www.amazon.co.uk") || pageSource.contains("www.amazon.com")) {
				status = false;
			} else {
				myAction.keyDown(Keys.CONTROL).sendKeys(Keys.END).keyUp(Keys.CONTROL).build().perform();
				myWait.until(ExpectedConditions.elementToBeClickable(By.id("pnnext")));
				//driver.findElement(By.xpath("//span[contains(text(),'Next')]")).click();
				driver.findElement(By.linkText("Next")).click();
				// driver.findElement(By.id("pnnext")).click();
			}
		}
	}

	@When("^I click on it$")
	public void i_click_on_it() {
		driver.findElement(By.xpath("//a[contains(text(),'Amazon')]")).click();
	}

	@Then("^I check if the product is iPhone smartPhone$")
	public void i_check_if_the_product_is_iPhone_smartPhone() {
		storageList = new ArrayList<WebElement>();
		boolean found = driver.findElement(By.id("dp-container")).getText().contains("Size Name");
		if (!found) {
			driver.findElement(By.id("twotabsearchtextbox")).sendKeys("iPhone");
			driver.findElement(By.xpath("//input[@value='Go']")).submit();
			driver.findElement(By.xpath("//ul[@id='s-results-list-atf']/li[1]")).click();
		}
	}

	@Then("^I identify highest memory$")
	public void i_identify_highest_memory() throws Throwable {
		storageList = driver.findElements(By.xpath("//*[@id='variation_size_name']/ul/li"));
		StringBuffer sBuffer = new StringBuffer();
		for (WebElement storage : storageList) {
			p = Pattern.compile("\\d");
			m = p.matcher(storage.getText());
			while (m.find()) {
				sBuffer.append(m.group());
			}
			memory = Long.parseLong(sBuffer.toString());
			if (memory > highestMemory) {
				highestMemory = memory;
				highestMemoryElement = storage.findElement(By.cssSelector("button"));
			}
		}
		highestMemoryElement.click();

		myWait = new WebDriverWait(driver, 20);
		previousUrl = driver.getCurrentUrl();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		ExpectedCondition expCond = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return (d.getCurrentUrl() != previousUrl);
			}
		};
		myWait.until(expCond);
		amazonUrl = driver.getCurrentUrl();
		System.out.println("amzonUrl"+amazonUrl);
		modelName = driver.findElement(By.id("productTitle")).getText();
		modelPrice = driver.findElement(By.id("priceblock_ourprice")).getText();
	}

	@When("^I print product information$")
	public void i_print_product_information() throws Throwable {
		System.out.println("Selected iPhone Model is " + modelName);
		System.out.println("Price of the product is " + modelPrice);
		System.out.println("Clicked URL is " + amazonUrl);
	}

	@Then("^I close the browser$")
	public void i_close_the_browser() throws Throwable {
		Thread.sleep(3000);
		driver.close();
	}

}