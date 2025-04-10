import { AuthForm } from "@/components/auth/auth-form"

export default function AuthPage() {
  return (
    <div className="flex min-h-screen flex-col">
      <div className="flex flex-1">
        <div className="flex flex-1 flex-col justify-center px-4 py-12 sm:px-6 lg:flex-none lg:px-20 xl:px-24">
          <div className="mx-auto w-full max-w-sm lg:w-96">
            <div className="flex items-center">
              <span className="text-xl font-bold">SnapScreen</span>
            </div>
            <div className="mt-8">
              <AuthForm />
            </div>
          </div>
        </div>
        <div className="relative hidden w-0 flex-1 lg:block">
          <div className="absolute inset-0 h-full w-full bg-black dark:bg-zinc-900">
            <div className="flex h-full items-center justify-center p-8">
              <div className="max-w-2xl text-white">
                <blockquote className="mt-6 text-xl font-semibold">
                  "SnapScreen has made my job application process much more efficient. I can now quickly check how well my resume matches job descriptions and make targeted improvements."
                </blockquote>
                <div className="mt-6">
                  <p className="font-medium">Alex Morgan</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
} 