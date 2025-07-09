package collectjobs;

import dataorganize.DataToExcel;
import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AltenJobCollector {

    CompaniesLinks companiesLinks = new CompaniesLinks();

    WebDriver driver = new EdgeDriver();

    private final HashMap<Integer, List<String>> id_jobInfo = new HashMap<>();
    private final HashMap<Integer, String> jobsLinks = new HashMap<>();
    private final HashMap<Integer, StringBuilder> id_jobQualifications = new HashMap<>();
    private final HashMap<Integer, StringBuilder> id_jobMissions = new HashMap<>();

    public HashMap<Integer, StringBuilder> getId_jobQualifications() {
        return id_jobQualifications;
    }

    public HashMap<Integer, StringBuilder> getId_jobMissions() {
        return id_jobMissions;
    }

    public HashMap<Integer, String> getJobsLinks() {
        return jobsLinks;
    }

    public HashMap<Integer, List<String>> getId_jobInfo() {
        return id_jobInfo;
    }

    public void setUpDriver(){
        String link = companiesLinks.altenLink;
        driver.get(link);
    }

    public void closeDriver(){
        driver.quit();
    }

    public void getJobsInformations(){

        int job_id = 0;

        List<String> domainsLinks = new ArrayList<>();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        try {

            /*
             * Here, we access to the Domains block and collect the links of the
             * existing domains
             */
            WebElement domainsSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("expertise")
            ));

            List<WebElement> listDomainDivs = domainsSection.findElements(
                    By.cssSelector(
                            "div.container-text div.g-4.justify-content-center.row.row-cols-1.row-cols-md-3.row-cols-sm-2.wp-block-bootstrap-row div.col.wp-block-bootstrap-column a")
            );

            for(WebElement element : listDomainDivs){
                domainsLinks.add(element.getAttribute("href"));
            }


            /*
            * Here, we iterate over the domains, each domain contains a list of jobs.
            * For each job we collect its data.
            * */

            for(String link : domainsLinks){
                driver.get(link);
                Thread.sleep(2000);


                handleCookieBanner(driver, wait);
                WebElement jobsSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("div.container-md ul.nav.tabs__nav.d-none.d-md-flex")
                ));

                List<WebElement> li_jobsList = jobsSection.findElements(By.tagName("li"));
                for(WebElement li : li_jobsList){

                    String jobTitle = li.getText();
                    System.out.println(jobTitle);
                    safeClick(driver, li);

                    WebElement missionsSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("div.container-md div.tab-content div.tab-pane.fade.wp-block-bootstrap-tab-item.show.active")
                    ));

                    List<WebElement> li_elements = missionsSection.findElements(By.tagName("li"));
                    for(int i=0; i<li_elements.size(); i++){
                          System.out.println(li_elements.get(i).getText());
//                        String missions = "";
//                        List<WebElement> li_elements = div_elements.get(i).findElements(By.tagName("li"));
//                        for(WebElement li_element : li_elements){
//                            missions = missions + li_element.getText();
//                        }
//                        System.out.println("**********************************************************************************************   "+missions);
                    }
                    Thread.sleep(1000);
                }







//                WebElement jobsSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
//                        By.cssSelector("div.container-md div.tab-content")
//                ));
//                List<WebElement> a_elements = jobsSection.findElements(By.tagName("a"));
//                List<WebElement> div_elements = jobsSection.findElements(By.tagName("div"));
//                for(int i=0; i<a_elements.size(); i++){
//
//                    String jobTitle = "";
//                    String hiddenJobTitle = (String) ((JavascriptExecutor) driver).executeScript(
//                            "return arguments[0].textContent.trim();",
//                            a_elements.get(i)
//                    );
//
//                    String missions = "";
//                    List<WebElement> li_elements = div_elements.get(i).findElements(By.tagName("li"));
//                    for(WebElement li_element : li_elements){
//                        missions = missions + li_element.getText();
//                    }
//                    System.out.println(missions);
//                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void safeClick(WebDriver driver, WebElement element) {
        try {
            // Scroll into view and click using JS to avoid overlays
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
            Thread.sleep(500); // allow scroll animation to complete

            // Try regular click
            element.click();
        } catch (ElementClickInterceptedException e) {
            // Fallback to JS click
            System.out.println("Click intercepted, using JavaScript click instead.");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        } catch (Exception ex) {
            System.out.println("Error clicking element: " + ex.getMessage());
        }
    }


    public void handleCookieBanner(WebDriver driver, WebDriverWait wait) {
        try {
            // Wait up to 5s for the banner to appear (or skip if it doesn't)
            List<WebElement> banners = driver.findElements(By.id("tarteaucitronAlertBig"));
            if (!banners.isEmpty()) {
                WebElement banner = banners.get(0);

                if (banner.isDisplayed() || banner.getCssValue("opacity").equals("1")) {
                    System.out.println("Cookie banner found. Trying to accept or hide...");

                    // Try to click the accept button
                    List<WebElement> acceptButtons = driver.findElements(By.id("tarteaucitronAllAllowed"));
                    if (!acceptButtons.isEmpty()) {
                        try {
                            acceptButtons.get(0).click();
                            wait.until(ExpectedConditions.invisibilityOf(banner));
                            System.out.println("Accepted cookies, banner dismissed.");
                        } catch (Exception e) {
                            System.out.println("Accept button not clickable, using JS to hide the banner.");
                            JavascriptExecutor js = (JavascriptExecutor) driver;
                            js.executeScript("arguments[0].style.display='none';", banner);
                        }
                    } else {
                        System.out.println("No accept button found, using JS to hide the banner.");
                        JavascriptExecutor js = (JavascriptExecutor) driver;
                        js.executeScript("arguments[0].style.display='none';", banner);
                    }
                } else {
                    System.out.println("Banner exists but is not visible or blocking.");
                }
            } else {
                System.out.println("No cookie banner found in DOM.");
            }

        } catch (Exception e) {
            System.out.println("Error handling cookie banner: " + e.getMessage());
        }
    }




    public static void main(String[] args) {
        AltenJobCollector altenJobCollector = new AltenJobCollector();
        altenJobCollector.setUpDriver();
        altenJobCollector.getJobsInformations();
        altenJobCollector.closeDriver();

//        DataToExcel dataToExcel = new DataToExcel();
//        dataToExcel.createWorkbook();
//        dataToExcel.applyBorderStyle();
//        dataToExcel.applyHeaderStyle();
//        dataToExcel.writeTableHeader();
//        dataToExcel.writeData("Alten", altenJobCollector.getId_jobInfo(), altenJobCollector.getJobsLinks(), altenJobCollector.getId_jobMissions(), altenJobCollector.getId_jobQualifications());
//
//        dataToExcel.saveWorkbook("C://Users//touhafi//Desktop//outputAlten.xlsx");
    }
}
