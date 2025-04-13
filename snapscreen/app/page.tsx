import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { FileText, ArrowRight, Zap, Shield, Search } from "lucide-react";
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from "@/components/ui/accordion";
import { AppNavbar } from "@/components/app-navbar";

export default function HomePage() {
  return (
    <div className="flex min-h-screen w-full flex-col">
      <AppNavbar />
      <div className="w-full">
        <main>
          {/* Hero section */}
          <section className="w-full py-12 md:py-24 lg:py-32">
            <div className="container mx-auto px-4 md:px-6">
              <div className="grid gap-6 lg:grid-cols-[1fr_400px] lg:gap-12 xl:grid-cols-[1fr_600px]">
                <div className="flex flex-col justify-center space-y-4">
                  <div className="space-y-2">
                    <h1 className="text-3xl font-bold tracking-tighter sm:text-4xl md:text-5xl lg:text-6xl">
                      Optimize your resume for every job application
                    </h1>
                    <p className="max-w-[700px] text-gray-500 md:text-xl dark:text-gray-400">
                      Upload your resume, paste a job description, and get instant insights on how to improve your resume for ATS systems.
                    </p>
                  </div>
                  <div className="flex flex-col gap-2 min-[400px]:flex-row">
                    <Link href="/dashboard" passHref>
                      <Button size="lg">
                        Get Started
                        <ArrowRight className="ml-2 h-4 w-4" />
                      </Button>
                    </Link>
                  </div>
                </div>
                <div className="flex items-center justify-center">
                  <Card className="w-full">
                    <CardHeader>
                      <CardTitle>Resume Analysis</CardTitle>
                      <CardDescription>Match score: 82%</CardDescription>
                    </CardHeader>
                    <CardContent>
                      <div className="flex justify-between items-center mb-4">
                        <div className="flex items-center">
                          <FileText className="mr-2 h-5 w-5 text-blue-500" />
                          <span className="text-sm">resume-software-engineer.pdf</span>
                        </div>
                        <div className="bg-green-100 text-green-800 text-xs px-2 py-1 rounded-full">
                          ATS Friendly
                        </div>
                      </div>
                      <div className="space-y-2">
                        <div className="bg-gray-100 p-2 rounded-md flex items-center justify-between">
                          <span>Hard Skills Match</span>
                          <span className="font-semibold">8/10</span>
                        </div>
                        <div className="bg-gray-100 p-2 rounded-md flex items-center justify-between">
                          <span>Keyword Optimization</span>
                          <span className="font-semibold">Good</span>
                        </div>
                        <div className="bg-gray-100 p-2 rounded-md flex items-center justify-between">
                          <span>Format & Structure</span>
                          <span className="font-semibold">Excellent</span>
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                </div>
              </div>
            </div>
          </section>

          {/* Features section */}
          <section className="w-full py-12 md:py-24 lg:py-32 bg-gray-50 dark:bg-gray-900">
            <div className="container mx-auto px-4 md:px-6">
              <div className="flex flex-col items-center justify-center space-y-4 text-center">
                <div className="space-y-2">
                  <h2 className="text-3xl font-bold tracking-tighter sm:text-4xl md:text-5xl">
                    Why use Snapscreen?
                  </h2>
                  <p className="mx-auto max-w-[700px] text-gray-500 md:text-xl dark:text-gray-400">
                    Our cutting-edge resume scanner gives you the edge in today's competitive job market.
                  </p>
                </div>
              </div>
              <div className="mx-auto grid max-w-5xl grid-cols-1 gap-6 md:grid-cols-3 lg:gap-12 mt-12">
                <Card className="flex flex-col p-5 h-full">
                  <div className="flex flex-col items-center">
                    <div className="bg-gray-100 dark:bg-gray-800 rounded-full p-4 w-20 h-20 flex items-center justify-center mb-5">
                      <Search className="h-8 w-8" />
                    </div>
                    <h3 className="text-xl font-medium mb-3 text-center">ATS Optimization</h3>
                    <p className="text-gray-500 dark:text-gray-400 text-center">
                      Ensure your resume gets past Applicant Tracking Systems with keyword suggestions and format checks.
                    </p>
                  </div>
                </Card>
                
                <Card className="flex flex-col p-5 h-full">
                  <div className="flex flex-col items-center">
                    <div className="bg-gray-100 dark:bg-gray-800 rounded-full p-4 w-20 h-20 flex items-center justify-center mb-5">
                      <Zap className="h-8 w-8" />
                    </div>
                    <h3 className="text-xl font-medium mb-3 text-center">Instant Analysis</h3>
                    <p className="text-gray-500 dark:text-gray-400 text-center">
                      Receive immediate feedback on your resume with detailed insights on how to improve it.
                    </p>
                  </div>
                </Card>
                
                <Card className="flex flex-col p-5 h-full">
                  <div className="flex flex-col items-center">
                    <div className="bg-gray-100 dark:bg-gray-800 rounded-full p-4 w-20 h-20 flex items-center justify-center mb-5">
                      <Shield className="h-8 w-8" />
                    </div>
                    <h3 className="text-xl font-medium mb-3 text-center">Job-Specific</h3>
                    <p className="text-gray-500 dark:text-gray-400 text-center">
                      Tailor your resume for each job application to maximize your chances of getting an interview.
                    </p>
                  </div>
                </Card>
              </div>
            </div>
          </section>

          {/* Q&A Section */}
          <section className="w-full py-12 md:py-24 lg:py-32">
            <div className="container mx-auto px-4 md:px-6">
              <div className="flex flex-col items-center justify-center space-y-4 text-center mb-10">
                <div className="space-y-2">
                  <h2 className="text-3xl font-bold tracking-tighter sm:text-4xl md:text-5xl">
                    Questions & Answers
                  </h2>
                </div>
              </div>
              
              <div className="mx-auto max-w-3xl">
                <Accordion type="single" collapsible className="w-full">
                  <AccordionItem value="item-1">
                    <AccordionTrigger className="text-lg font-medium text-left">
                      What is a resume scanner? Why is it better than manually optimizing?
                    </AccordionTrigger>
                    <AccordionContent className="text-gray-600 pt-4">
                      <p className="mb-3">
                        A resume scanner analyzes your resume against job descriptions to identify gaps and suggest improvements. It works similarly to Applicant Tracking Systems (ATS) used by employers.
                      </p>
                      <p>
                        While you could manually optimize your resume, our AI-powered scanner automatically identifies missing keywords, formatting issues, and other opportunities to improve. This saves you hours of work and guesswork for each job application, dramatically increasing your chances of getting an interview.
                      </p>
                    </AccordionContent>
                  </AccordionItem>
                  
                  <AccordionItem value="item-2">
                    <AccordionTrigger className="text-lg font-medium text-left">
                      What uniquely sets Snapscreen apart from other resume scanners?
                    </AccordionTrigger>
                    <AccordionContent className="text-gray-600 pt-4">
                      <p className="mb-3">
                        While there are other resume scanners available, Snapscreen stands out with several distinctive features:
                      </p>
                      <ul className="list-disc pl-6 space-y-2">
                        <li>Designed specifically for modern ATS systems with the latest parsing algorithms</li>
                        <li>Provides section-by-section analysis rather than just a generic score</li>
                        <li>Offers detailed hard and soft skills matching against each job description</li>
                        <li>Focuses on privacy - we don't store your resume data after analysis</li>
                      </ul>
                    </AccordionContent>
                  </AccordionItem>
                  
                  <AccordionItem value="item-3">
                    <AccordionTrigger className="text-lg font-medium text-left">
                      How should I use the scan results to improve my resume?
                    </AccordionTrigger>
                    <AccordionContent className="text-gray-600 pt-4">
                      <p className="mb-3">
                        After scanning your resume, you'll receive actionable insights in several categories:
                      </p>
                      <ul className="list-disc pl-6 space-y-2">
                        <li><strong>Searchability:</strong> Address any missing contact information or sections</li>
                        <li><strong>Hard Skills:</strong> Add missing technical skills that appear in the job description</li>
                        <li><strong>Soft Skills:</strong> Incorporate relevant soft skills throughout your experience descriptions</li>
                        <li><strong>Formatting:</strong> Fix any formatting issues that could prevent ATS systems from properly parsing your resume</li>
                      </ul>
                      <p className="mt-3">
                        We recommend making these changes for each job application to maximize your chances of getting past the initial screening.
                      </p>
                    </AccordionContent>
                  </AccordionItem>
                </Accordion>
              </div>
            </div>
          </section>

          {/* Call to action */}
          <section className="w-full py-12 md:py-24 lg:py-32">
            <div className="container mx-auto px-4 md:px-6">
              <div className="flex flex-col items-center justify-center space-y-4 text-center">
                <div className="space-y-2">
                  <h2 className="text-3xl font-bold tracking-tighter sm:text-4xl md:text-5xl">
                    Ready to optimize your resume?
                  </h2>
                  <p className="mx-auto max-w-[700px] text-gray-500 md:text-xl dark:text-gray-400">
                    Get started for free and increase your chances of landing that dream job.
                  </p>
                </div>
                <div className="mx-auto w-full max-w-sm space-y-2">
                  <Link href="/dashboard" passHref>
                    <Button className="w-full" size="lg">
                      Get Started Now
                    </Button>
                  </Link>
                </div>
              </div>
            </div>
          </section>
        </main>
      </div>
    </div>
  );
} 