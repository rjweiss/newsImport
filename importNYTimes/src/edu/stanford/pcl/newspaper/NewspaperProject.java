package edu.stanford.pcl.newspaper;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Rebecca
 * Date: 4/28/12
 * Time: 6:12 PM
 * To change this template use File | Settings | File Templates.
 */

public class NewspaperProject {
    public static void main(String[] args) throws Exception {
//        Mongo m = new Mongo();
//        DB db = m.getDB("test");
//        DBCollection articles = db.getCollection("articles");
//        DBObject myDoc = articles.findOne();
//        DBCursor cur;
//        BasicDBObject query = new BasicDBObject();

//        Calendar cal = new GregorianCalendar();
//        cal.set(2010, 0, 1); // retrieving from January 1st, 2010
//        Date fromDate = cal.getTime();
//        query.put("date", new BasicDBObject("$gte", fromDate));
//        System.out.println(articles.count(query)); //returns 157134 total articles

        ArrayList docs = new ArrayList();

        String doc1 = "Supporters of Bailout Claim Victory in Greek Election\n" +
                "By RACHEL DONADIO\n" +
                "ATHENS — Greek voters on Sunday gave a narrow victory in parliamentary elections to a party that had supported a bailout for the country’s failed economy. The vote was widely seen as a last chance for Greece to remain in the euro zone, and the results had an early rallying effect on world markets.\n" +
                "\n" +
                "Greece’s choice was welcomed by the finance ministers of the euro zone countries, who in a statement on Sunday night in Brussels said the outcome of the vote “should allow for the formation of a government that will carry the support of the electorate to bring Greece back on a path of sustainable growth.”\n" +
                "\n" +
                "While the election afforded Greece a brief respite from a rapid downward spiral, it is not likely to prevent a showdown between the next government and the country’s so-called troika of foreign creditors — the European Commission, the European Central Bank and the International Monetary Fund — over the terms of a bailout agreement.\n" +
                "\n" +
                "Even the most pro-Europe of Greece’s political parties, the conservative New Democracy, which came in first, has said a less austere agreement is crucial to a country with a 22 percent unemployment rate and the rising prospect of social unrest.\n" +
                "\n" +
                "The euro zone ministers pledged to help Greece transform its economy and said continued fiscal and structural changes were the best way to cope with its economic challenges. “The Eurogroup reiterates its commitment to assist Greece in its adjustment effort in order to address the many challenges the economy is facing,” the statement said.\n" +
                "\n" +
                "The ministers added that representatives of Greece’s creditors would discuss emergency loans and changes as soon as a government was in place. Much of the negotiations are expected to fall to Germany, a strong proponent of austerity. It and other euro zone countries must also consider the needs of the larger economies of Spain and Italy, which are also under intense pressure.\n" +
                "\n" +
                "Official projections showed New Democracy with 30 percent of the vote and 128 seats in the 300-seat Parliament. The Syriza party, which had surged on a wave of anti-austerity sentiment and spooked Europe with its talk of tearing up Greece’s loan agreement with its foreign creditors, was in second place, with 27 percent of the vote and 72 seats. Syriza officials had rejected calls for a coalition, ensuring its role as a vocal opposition bloc to whatever government emerges.\n" +
                "\n" +
                "But unlike in the May 6 election, when New Democracy placed first but was unable to form a government, this time intense international pressure, and the fact that the Greek government is quickly running out of money, made it likely there would be a coalition with New Democracy’s longtime rival, the socialist Pasok party. Pasok placed third in the voting, with 12 percent of the vote and 33 seats. The extreme right Golden Dawn party got 18 seats.\n" +
                "\n" +
                "Investors gave an early thumbs-up on Sunday night, pushing up the euro in value against the dollar. “It looks like we’ve avoided the worst-case scenario,” said Darren Williams, a European economist for AllianceBernstein in London. “I think that’s important, because we could have gone to a very bad place very quickly.”\n" +
                "\n" +
                "Previous rallies in response to developments in Europe were short-lived. A few weeks ago, markets initially responded positively to a bailout plan for Spanish banks, but that optimism quickly gave out when the American stock markets opened that Monday.\n" +
                "\n" +
                "The health of the world economy weighs heavily on the United States, and on President Obama’s re-election campaign. In recent days, Mr. Obama has increased the pressure on European leaders to find longer-term solutions to shoring up the euro. A White House statement on Sunday said, “As President Obama and other world leaders have said, we believe that it is in all our interests for Greece to remain in the euro area while respecting its commitment to reform.”\n" +
                "\n" +
                "In a sign of the high stakes for global financial stability, the finance ministers, the White House and the European Commission urged Greek political leaders to form a government quickly. Under the current loan agreement, the next government has just weeks to determine how to slash the equivalent of 5 percent of the country’s gross domestic product to meet budget-reduction targets.\n" +
                "\n" +
                "In a victory statement on Sunday evening, the New Democracy leader, Antonis Samaras, called for the formation of a government of national unity aimed at keeping Greece in the euro zone and renegotiating the loan agreement. “There is no time for political games. The country must be governed,” he said, adding, “We will cooperate with our European partners to boost growth and tackle the torturous problem of unemployment.”\n" +
                "\n" +
                "Alexis Tsipras, the 37-year-old leader of Syriza, conceded defeat. “We fought against blackmail to put an end to memorandum,” he said, referring to the loan agreement. “We’re proud of our fight.”\n" +
                "\n" +
                "He added that Syriza would be “present in developments from the position of the main opposition party.”\n" +
                "\n" +
                "Any new leader will face an uphill battle to inject confidence into a paralyzed Greek economy that depends heavily on the continued infusion of money from its only remaining lifeline, the European Central Bank. The Greek economy and a deficit-ridden government have lost most of their ability to raise new revenues or borrow money to continue operations.\n" +
                "\n" +
                "But political analysts said no matter what government was formed, it would be weak and likely short-lived, lacking deep popular support and the broader confidence of Europe. And it was unlikely that the election results would persuade Greece’s European lenders to extend loans without economic reforms and drastic spending cuts.\n" +
                "\n" +
                "Mr. Samaras “won a Pyrrhic victory,” said Harry Papasotiriou, a political-science professor at Panteion University in Athens. “New Democracy will try to renegotiate part of the memorandum agreement, but they won’t get far, and then they will have to implement within 100 days a very difficult program of reforms. And the unions of the public sector, supported by the radical left, will give him a hard time.”\n" +
                "\n" +
                "Asked what the election results change, Daniel Gros, the director of the Centre for European Policy Studies, which is based in Brussels, said, “Unfortunately nothing.” He said the government would most likely not be strong enough to enact the structural changes needed to turn around Greece’s uncompetitive economy.\n" +
                "\n" +
                "Meanwhile, Greece’s partners in the euro zone are growing impatient. “I think there’s no desire for them to leave, but very little inclination to say, ‘Let’s give them still another chance’ and give any substantial compromise on the rescue package,” Mr. Gros said.\n" +
                "\n" +
                "For many Greeks, the election was a choice between hope and fear. Syriza had billed itself as a kind of “Greek Spring,” capturing the momentum of those hungry for change at almost any cost from a political system that is widely seen as corrupt and ineffective. It also had support from voters who felt betrayed by the socialists, whose party was in power in 2010 when Greece signed the first of its two loan deals with foreign creditors.\n" +
                "\n" +
                "New Democracy tapped into different fears — of the unknown, of illegal immigration, of an exit from the euro zone. Its main campaign advertisement showed an elementary-school teacher telling his students which countries use the euro. When one asks, “And what about Greece?” the teacher stares back in stony silence. “Why, teacher, why?” the student asks.\n" +
                "\n" +
                "In the end, fear of imminent collapse, as opposed to the slow death of their economy and society, appeared to drive a majority of Greeks to New Democracy.\n" +
                "\n" +
                "As he watched election returns at an outdoor cafe here, Nikos Theodossiades, 69, said he was glad that New Democracy had won. “This is the only way for the country to move forward,” he said. “Staying in the euro zone, despite all its problems, is much better than the alternative.”\n" +
                "\n" +
                "Panagiotis Pierrakis, 48, an Athens taxi driver, said that he had voted for Syriza and that although it did not win, he thought the results had sent a message. The election, he said, was “a message” to Europe that “you are not the boss — Mrs. Merkel, or anybody,” a reference Chancellor Angela Merkel of Germany. “We want somebody from our country to oversee our economic system.”\n" +
                "\n" +
                "Niki Kitsantonis and Jim Yardley contributed reporting.";

        String doc2 = "In Romney and Obama Speeches, Selective Truths\n" +
                "By PETER BAKER and MICHAEL COOPER\n" +
                "To listen to Mitt Romney tell it, President Obama is a job-killing, free-spending, big-government liberal who made the recession worse with his policies and endangered free-market capitalism. The president has spent tax dollars “at a pace without precedent in recent history,” Mr. Romney says, and “added almost as much debt as all the prior presidents combined.”\n" +
                "\n" +
                "As Mr. Obama travels the country, he offers the opposite self-portrait, that of a job-creating, tightfisted, government-shrinking pragmatist who saved the country from another Great Depression. On his watch, “government employment has gone down,” he says, and federal spending has increased “at the lowest pace in nearly 60 years.”\n" +
                "\n" +
                "With the presidential race largely focused on the economy and the budget, Mr. Obama and Mr. Romney are filling speeches with facts and figures designed to enhance their case and diminish the other guy’s, in the process often making assertions fundamentally at odds with one another. Along the way, both candidates are at times stretching the truth, using statistics without context, exaggerating their own records and misrepresenting their opponent’s.\n" +
                "\n" +
                "Each side regularly accuses the other of lying, and in any campaign there is a temptation to write both sides off, as if every misleading statement were equivalent. In reality, some are more fundamental than others, more egregious, more central to the larger argument. Mr. Romney, for example, called his 2010 book laying out the rationale for his candidacy “No Apology” — charging, falsely in the eyes of many independent fact-checkers, that Mr. Obama had traveled the world apologizing for America.\n" +
                "\n" +
                "But determining who is the worse dissembler can be a subjective exercise, even in an age when news organizations, blogs and partisan groups blitz out regular fact-checks. When PolitiFact, the Pulitzer-winning project of The Tampa Bay Times, evaluated the statements, it found that more of  Mr. Romney’s were  misleading than Mr. Obama’s. Glenn Kessler’s Fact Checker column at The Washington Post, on the other hand, has awarded roughly the same average “Pinocchio scores” — his measure of falsity — to both men.\n" +
                "\n" +
                "Sometimes the truth or dishonesty of an assertion depends on definitions or on when you start counting. When it comes to unemployment, for instance, Mr. Romney counts from the month when Mr. Obama took office and inherited an economy that was hemorrhaging jobs at historic rates. Mr. Obama prefers to count from 2010, when his policies arguably had started to take effect and the job picture had begun turning around, eschewing blame for the losses at the start of his term.\n" +
                "\n" +
                "But one thing is clear: While they claim distance from typical Washington politics, both men have mastered the ancient Washington art of selective storytelling.\n" +
                "\n" +
                "Job Creation\n" +
                "\n" +
                "Mr. Obama: “Over 4 million jobs created in the last two years.”\n" +
                "\n" +
                "Mr. Romney: “He has not created jobs.”\n" +
                "\n" +
                "Nowhere is the veracity of the candidates’ statements more central than on the economic issues at the heart of their contest. The question of job creation provides a quintessential case study. By counting from January 2009, Mr. Romney paints the bleak portrait of a country that despite a modest recovery still has fewer jobs than it did when Mr. Obama took office. According to the Bureau of Labor Statistics, the country has 552,000 fewer jobs now than when the president was inaugurated.\n" +
                "\n" +
                "Mr. Obama starts his count in March 2010, after the money from his stimulus law was flowing and payrolls began growing again. And he generally cites private sector employment, so that he can say 4.3 million jobs have been created. Counting government workforces, the job growth is actually lower, 3.8 million jobs, according to the bureau.\n" +
                "\n" +
                "Is it reasonable to start counting in January 2009? The economy was already shedding hundreds of thousands of jobs a month by then and none of Mr. Obama’s policies would take effect for some time. Starting the count just one month later would show a small net increase in jobs for the president’s tenure in office. Yet if he cannot be blamed for job losses in the early months of his term, can Mr. Obama be held responsible for not replacing the lost jobs more quickly?\n" +
                "\n" +
                "Federal Spending\n" +
                "\n" +
                "Mr. Romney: “Since President Obama assumed office two and a half years ago, federal spending has accelerated at a pace without precedent in recent history.”\n" +
                "\n" +
                "Mr. Obama: “Since I’ve been president, federal spending has risen at the lowest pace in nearly 60 years.”\n" +
                "\n" +
                "Both men may be exaggerating in their own ways — and once again it depends in part on when you start counting. The president based his claim on a column in Market Watch, which calculated that government spending during the Obama years has grown 1.4 percent annually, the lowest since President Dwight D. Eisenhower.\n" +
                "\n" +
                "But that column started counting with the fiscal year that started on Oct. 1, 2009, more than eight months after Mr. Obama took office, on the theory that that was the first full fiscal year Mr. Obama could shape. It held Mr. Obama responsible for $140 billion in additional spending from those first eight months stemming largely from his stimulus spending program. The calculation also gave Mr. Obama credit for automatic spending cuts now slated for the next fiscal year, which the president opposes and which may not actually take effect.\n" +
                "\n" +
                "Using different ways to calculate spending, The Washington Post found that spending has been going up 5.2 percent a year under Mr. Obama, while The Associated Press put the figure at 3 percent, excluding 2009. Either way, it is neither the “lowest pace in nearly 60 years,” as Mr. Obama claimed, nor “at a pace without precedent in recent history,” as Mr. Romney charged.\n" +
                "\n" +
                "Going back to Eisenhower, federal spending has risen on average 7 percent a year, or 2.6 percent when adjusted for inflation. After the spending burst of 2009, Mr. Obama, constrained by Congress and aided by repayments of bank and auto bailouts, increased spending just 0.5 percent a year over the next three fiscal years when adjusted for inflation; only two other three-year periods since Eisenhower had lower spending growth.\n" +
                "\n" +
                "Another way to judge spending is to look at what was anticipated based on the laws and policies on the books at the time Mr. Obama took office versus what actually happened. In January 2009, days before he was inaugurated, the nonpartisan Congressional Budget Office forecast spending in the 2011 fiscal year, which ended last Sept. 30, at $3.323 trillion. In reality, after the policies put in place by Mr. Obama and Congress, it turned out to be $3.603 trillion, or 8.4 percent higher.\n" +
                "\n" +
                "Yet another way to look at spending is as a share of the overall economy. For years up to and through most of George W. Bush’s presidency, federal spending generally totaled about 20 percent or less of the nation’s overall economic output. In Mr. Bush’s final full fiscal year in office, ending Sept. 30, 2008, it rose to 20.8 percent.\n" +
                "\n" +
                "The next fiscal year, in which Mr. Bush was president for four months and Mr. Obama for eight, it shot up to 25.2 percent, then dipped to 24.1 percent for each of the next two years, the highest levels since World War II. Of course one reason spending is a larger share of the economy is because the overall economy shrunk during the recession. But even if spending rose starkly during the economic crisis under the presidencies of both Mr. Bush and Mr. Obama, it has remained at the higher level since then under Mr. Obama.\n" +
                "\n" +
                "Economics\n" +
                "\n" +
                "Mr. Romney: “We are only inches away from ceasing to be a free economy.”\n" +
                "\n" +
                "In attacking Mr. Obama’s stewardship of the economy, Mr. Romney has engaged in hyperbole at times — as with this claim. He has also contended that Mr. Obama made the recession “worse” and “last longer” and said that the president’s health care law would increase government spending to nearly half of the American economy.\n" +
                "\n" +
                "There is plenty of debate over how effective Mr. Obama’s economic policies have been, especially given the painfully slow recovery, but economists do not generally claim that Mr. Obama’s policies worsened the recession or made it longer.\n" +
                "\n" +
                "The 18-month recession officially ended in June 2009, five months into Mr. Obama’s term, as measured by the National Bureau of Economic Research. And even critics who consider the president’s stimulus package a missed opportunity — from liberals who say it was too small to conservatives who say it was wasteful and poorly targeted — tend to acknowledge what the Congressional Budget Office has found: it did save and create jobs, lower unemployment and help the economy grow in the short term.\n" +
                "\n" +
                "Despite Mr. Romney’s claim, the new health care law will not drive government spending up to half of the economy, unless all health care spending in the country is reclassified as government spending. Given that the new law still relies heavily on private insurance provided by employers, it is a stretch to treat all of that as if it were government expenditures.\n" +
                "\n" +
                "Experts predict the new health care law will drive up health spending, as more people will be covered, but not by a huge share of the economy. The chief actuary for the Centers for Medicare and Medicaid Services estimated that with the new law, health care spending will rise to 21 percent of the economy in 2019. Without the new law, it estimated, it would have risen to 20.8 percent of the economy.\n" +
                "\n" +
                "The Congressional Budget Office recently estimated what would happen to federal spending under a number of different situations, taking the new health care law into account. The projection showing the biggest jump found that assuming that a number of current tax cuts are extended and several budget cuts currently scheduled to take effect next year are averted, federal spending will rise to 29 percent of the economy in 2030 from 24 percent in 2011. But that rise is attributable in large part to rising interest payments required by ballooning debt.\n" +
                "\n" +
                "And the idea that the United States has almost ceased to be a free-market economy was labeled “ridiculously false” by PolitiFact, which cited an economic freedom index (from the Heritage Foundation, a conservative research group, which ranks the United States the world’s 10th freest economy of 179 judged.\n" +
                "\n" +
                "Federal Debt\n" +
                "\n" +
                "Mr. Romney: President Obama has “added almost as much debt as all the prior presidents combined.”\n" +
                "\n" +
                "The total national debt now stands at $15.8 trillion, up from $10.6 trillion when Mr. Obama took office, an increase of nearly 50 percent. A commonly cited and more economically important subset, debt held by the public, has grown to $11 trillion from $6.3 trillion, a 75 percent increase — closer to Mr. Romney’s claim, but still $1.7 trillion short of matching the accumulated public debt of the first 43 presidents. Adjusted for inflation, the debt Mr. Obama incurred is smaller relative to the combined prior debt.\n" +
                "\n" +
                "Tax Cuts\n" +
                "\n" +
                "Mr. Obama: “A record surplus was squandered on tax cuts for people who didn’t need them and weren’t even asking for them.”\n" +
                "\n" +
                "Mr. Obama makes it sound as if the surpluses projected at the end of President Bill Clinton’s tenure disappeared entirely because of Mr. Bush’s tax cuts. In fact, tax cuts for the wealthy were responsible for a small fraction of the disappearing surpluses.\n" +
                "\n" +
                "An analysis by the Congressional Budget Office last year showed that of $5.6 trillion in surpluses projected over 10 years when Mr. Bush took office, $1.6 trillion went to the tax cuts passed in 2001 and 2003, or 29 percent. The rest of the anticipated surpluses vanished because of increased spending and lower-than-expected revenues stemming from economic downturns.\n" +
                "\n" +
                "Mr. Obama supports keeping the tax cuts for all but the wealthiest 2 percent of Americans, or roughly 18 percent of the total cost. Extrapolated, that means the tax cuts “for people who didn’t need them” would have accounted for just 5 percent of the disappearing surpluses.\n" +
                "\n" +
                "Size of Government\n" +
                "\n" +
                "Mr. Obama: “The only time government employment has gone down during a recession has been under me.”\n" +
                "\n" +
                "Again, it depends on how it is counted. Looking at the five months from Mr. Obama’s inauguration to June 2009, when the recession officially ended, and including state and local governments, which are not under his direct control, government employment at all levels was virtually unchanged, from 22,576,000 in January 2009 to 22,570,000 in June, according to the Bureau of Labor Statistics.\n" +
                "\n" +
                "Total government employment was 21,969,000 in May 2012, a reduction of about 600,000 jobs. But looking only at the federal work force under Mr. Obama’s direct control, then government employment has actually gone up. He inherited a federal work force of 2,061,700, and it rose slightly through June 2009 to 2,109,700; by May of this year, it had grown further to 2,204,100, a 7 percent increase.\n" +
                "\n" +
                "Mr. Obama is not correct in saying this was the “only time” government shrunk during a recession. During the recession from July 1981 through November 1982, under President Ronald Reagan, and again during the recession from July 1990 through March 1991, under the elder President George Bush, government employment shrank slightly, both over all and in the federal work force specifically. The White House said he meant the recession and the 35 months following it.\n" +
                "\n" +
                "Either way, while Mr. Obama has boasted of shrinking the public work force, he has advocated policies to prevent it from shrinking by helping state and local governments avoid laying off employees. In recent days, he has lamented that continuing economic troubles result in part from a decreasing public work force.\n" +
                "\n" +
                "This article has been revised to reflect the following correction:\n" +
                "\n" +
                "Correction: June 19, 2012\n" +
                "\n" +
                "\n" +
                "An earlier version of this article misstated the total national debt. It is $15.8 trillion, not $15.7 trillion.\n" +
                "\n";

        docs.add(doc1);
        docs.add(doc2);

        Properties p = new Properties();
        p.put("annotators", "tokenize, ssplit, pos, lemma, parse, ner");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(p);

//        AbstractSequenceClassifier classifier = CRFClassifier.getClassifierNoExceptions("edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
        //can also just use CRFClassifier object
        
        //Testing NER for just one article
//        printNamedEntities(pipeline, classifier, (String)myDoc.get("body"));

        Iterator iterator = docs.iterator();

        while(iterator.hasNext())
            printNamedEntities(pipeline, iterator.next().toString());

    }

//    private static void printNamedEntities(StanfordCoreNLP pipeline, AbstractSequenceClassifier classifier, String text) {
    private static void printNamedEntities(StanfordCoreNLP pipeline, String text) {
        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        //only because ssplit
        for (CoreMap sentence : sentences) {
//            List<List<CoreLabel>> symbols = classifier.classify(sentence.toString());
            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
            //not necessary, should look at pipeline annotated document
            for (CoreLabel token : tokens) {
//                for (CoreLabel label : labels) {
                    String currentLabel = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
//                    String currentLabel = token.get(CoreAnnotations.AnswerAnnotation.class); // should look at NamedEntityTagAnnotation
                    String currentText = token.get(CoreAnnotations.TextAnnotation.class);
                    System.out.println(currentText + "(" + currentLabel + ")");
//                }
            }
        }
    }
}
