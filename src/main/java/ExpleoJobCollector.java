import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.List;

public class ExpleoJobCollector {
    public static void main(String[] args) {
        WebDriver driver = new EdgeDriver();
        driver.get("https://expleo-jobs-ma-fr.icims.com/jobs/search");

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            /*
             * Here we wait for the 5th iframe (index 4) to be available and automatically switch into it
             * This 4th iframe contains the dynamically loaded job listings.
             */
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(4));
            WebElement jobTable;
            jobTable = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("div.container-fluid.iCIMS_JobsTable")
                    )
            );
            List<WebElement> rows = jobTable.findElements(By.className("row"));

            for (WebElement row : rows) {
                //System.out.println("Row " + (i + 1) + ": " + rows.get(i).getText());
                WebElement job = row.findElement(By.cssSelector("div.col-xs-12.title a.iCIMS_Anchor h3"));
                String jobTitle = job.getText();

                /*
                 * The four DIVs inside the element ".col-xs-12 additionalFields" have a repetitive structure,
                 * for that we used the term ":nth-of-type(4)" which indicates which block number we wanna get.
                 * */
                String location = row.findElement(By.cssSelector("div.col-xs-12.additionalFields > dl > div.iCIMS_JobHeaderTag:nth-of-type(1) dd.iCIMS_JobHeaderData span")).getText();
                String contractType = row.findElement(By.cssSelector("div.col-xs-12.additionalFields > dl > div.iCIMS_JobHeaderTag:nth-of-type(3) dd.iCIMS_JobHeaderData span")).getText();
                String workMode = row.findElement(By.cssSelector("div.col-xs-12.additionalFields > dl > div.iCIMS_JobHeaderTag:nth-of-type(4) dd.iCIMS_JobHeaderData span")).getText();

                /* Here we access the job link to collect the job requirements */
                job.click();
                System.out.println(jobTitle + " - " + location + " - " + contractType + " - " + workMode);

            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());

        } finally {
            driver.quit();
        }
    }
}
